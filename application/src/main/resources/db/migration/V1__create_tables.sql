-- V1__create_tables.sql
-- Complete Multi-Schema Database Setup for Business SaaS Platform
-- Real-world design with proper entity relationships

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- SCHEMA: TENANT (module-tenant)
-- Owns: Users, Businesses, Roles, Settings, Subscriptions
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS tenant;

-- Tenant: Users table (platform users - can be owner, staff, customer)
CREATE TABLE tenant.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tenant: Businesses table (tenants - the actual businesses)
CREATE TABLE tenant.businesses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    owner_id UUID NOT NULL REFERENCES tenant.users(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    description TEXT,
    subdomain VARCHAR(255) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    website_url VARCHAR(500),
    country VARCHAR(100),
    city VARCHAR(100),
    address VARCHAR(500),
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tenant: Business settings table (1-to-1 with businesses)
CREATE TABLE tenant.business_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL UNIQUE REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    business_hours TEXT NOT NULL DEFAULT '{}',
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    shipping_cost_default DECIMAL(10,2),
    returns_policy_days INTEGER DEFAULT 30,
    language VARCHAR(50) DEFAULT 'en',
    theme_color VARCHAR(50),
    notification_preferences TEXT NOT NULL DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tenant: Subscriptions table (SaaS billing)
CREATE TABLE tenant.subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    plan_name VARCHAR(255) NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    max_orders INTEGER,
    max_staff INTEGER,
    features TEXT NOT NULL DEFAULT '{}',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

-- Tenant: Business user roles table (RBAC)
CREATE TABLE tenant.business_user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES tenant.users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    permissions TEXT NOT NULL DEFAULT '{}',
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================================================
-- SCHEMA: CATALOG (module-catalog)
-- Owns: Categories, Products, Variants, Reviews
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS catalog;

-- Catalog: Categories table
CREATE TABLE catalog.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Catalog: Products table (products and services)
CREATE TABLE catalog.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    category_id UUID REFERENCES catalog.categories(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    product_type VARCHAR(50) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'USD',
    images TEXT NOT NULL DEFAULT '{}',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Catalog: Product variants table (sizes, colors, durations, etc.)
CREATE TABLE catalog.product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    product_id UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    price_modifier DECIMAL(10,2),
    cost_modifier DECIMAL(10,2),
    stock_quantity INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Catalog: Reviews table
CREATE TABLE catalog.reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    product_id UUID REFERENCES catalog.products(id) ON DELETE SET NULL,
    customer_id UUID NOT NULL,
    staff_id UUID,
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

-- ============================================================================
-- CORE SCHEMA (module-core)
-- Owns: Shared entities across business (Customers, Inventory, Notifications)
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS core;

-- Core: Customers table (business customers - separate from platform users)
CREATE TABLE core.customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    user_id UUID REFERENCES tenant.users(id) ON DELETE SET NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    address TEXT,
    loyalty_points INTEGER DEFAULT 0,
    total_spent DECIMAL(15,2) DEFAULT 0,
    last_purchase TIMESTAMP
);

-- Core: Inventory tracking table
CREATE TABLE core.inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    product_variant_id UUID NOT NULL REFERENCES catalog.product_variants(id) ON DELETE CASCADE,
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    quantity_damaged INTEGER NOT NULL DEFAULT 0,
    last_stock_check TIMESTAMP
);

-- Core: Notifications table
CREATE TABLE core.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    business_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data TEXT NOT NULL DEFAULT '{}',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP
);

-- ============================================================================
-- SCHEMA: TRANSACTION (module-transaction)
-- Owns: Orders, Staff, Appointments, Payments, Refunds
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS transaction;

-- Transaction: Staff table (service providers/employees)
CREATE TABLE transaction.staff (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    user_id UUID REFERENCES tenant.users(id) ON DELETE SET NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    job_title VARCHAR(255),
    department VARCHAR(255),
    hourly_rate DECIMAL(10,2),
    availability_status VARCHAR(50) DEFAULT 'AVAILABLE',
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Transaction: Orders table
CREATE TABLE transaction.orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    customer_id UUID NOT NULL REFERENCES core.customers(id) ON DELETE RESTRICT,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    order_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method VARCHAR(50),
    payment_status VARCHAR(50),
    shipping_address TEXT,
    notes TEXT
);

-- Transaction: Order items table (line items)
CREATE TABLE transaction.order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    order_id UUID NOT NULL REFERENCES transaction.orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES catalog.products(id) ON DELETE RESTRICT,
    product_variant_id UUID REFERENCES catalog.product_variants(id) ON DELETE SET NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    notes TEXT
);

-- Transaction: Appointments table (service bookings)
CREATE TABLE transaction.appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    order_id UUID REFERENCES transaction.orders(id) ON DELETE SET NULL,
    order_item_id UUID REFERENCES transaction.order_items(id) ON DELETE SET NULL,
    customer_id UUID NOT NULL REFERENCES core.customers(id) ON DELETE RESTRICT,
    staff_id UUID NOT NULL REFERENCES transaction.staff(id) ON DELETE RESTRICT,
    product_id UUID NOT NULL REFERENCES catalog.products(id) ON DELETE RESTRICT,
    product_variant_id UUID REFERENCES catalog.product_variants(id) ON DELETE SET NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    location VARCHAR(500),
    notes TEXT,
    customer_notes TEXT,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE
);

-- Transaction: Payments table
CREATE TABLE transaction.payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    order_id UUID NOT NULL REFERENCES transaction.orders(id) ON DELETE RESTRICT,
    customer_id UUID NOT NULL REFERENCES core.customers(id) ON DELETE RESTRICT,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method VARCHAR(50) NOT NULL,
    payment_gateway VARCHAR(50),
    transaction_id VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    receipt_url VARCHAR(500),
    failure_reason TEXT
);

-- Transaction: Refunds table
CREATE TABLE transaction.refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    business_id UUID NOT NULL,
    order_id UUID NOT NULL REFERENCES transaction.orders(id) ON DELETE RESTRICT,
    payment_id UUID NOT NULL REFERENCES transaction.payments(id) ON DELETE RESTRICT,
    processed_by UUID REFERENCES transaction.staff(id) ON DELETE SET NULL,
    amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT
);

-- ============================================================================
-- INDEXES: TENANT SCHEMA
-- ============================================================================
CREATE INDEX idx_tenant_users_email ON tenant.users(email);
CREATE INDEX idx_tenant_businesses_subdomain ON tenant.businesses(subdomain);
CREATE INDEX idx_tenant_businesses_owner_id ON tenant.businesses(owner_id);
CREATE INDEX idx_tenant_subscriptions_business_id ON tenant.subscriptions(business_id);
CREATE INDEX idx_tenant_business_user_roles_business_id ON tenant.business_user_roles(business_id);
CREATE INDEX idx_tenant_business_user_roles_user_id ON tenant.business_user_roles(user_id);

-- ============================================================================
-- INDEXES: CATALOG SCHEMA
-- ============================================================================
CREATE INDEX idx_catalog_categories_business_id ON catalog.categories(business_id);
CREATE INDEX idx_catalog_products_business_id ON catalog.products(business_id);
CREATE INDEX idx_catalog_products_category_id ON catalog.products(category_id);
CREATE INDEX idx_catalog_product_variants_product_id ON catalog.product_variants(product_id);
CREATE INDEX idx_catalog_product_variants_sku ON catalog.product_variants(sku);
CREATE INDEX idx_catalog_reviews_business_id ON catalog.reviews(business_id);
CREATE INDEX idx_catalog_reviews_product_id ON catalog.reviews(product_id);
CREATE INDEX idx_catalog_reviews_customer_id ON catalog.reviews(customer_id);

-- ============================================================================
-- INDEXES: CORE SCHEMA
-- ============================================================================
CREATE INDEX idx_core_customers_business_id ON core.customers(business_id);
CREATE INDEX idx_core_customers_user_id ON core.customers(user_id);
CREATE INDEX idx_core_inventory_business_id ON core.inventory(business_id);
CREATE INDEX idx_core_inventory_product_variant_id ON core.inventory(product_variant_id);
CREATE INDEX idx_core_notifications_business_id ON core.notifications(business_id);
CREATE INDEX idx_core_notifications_recipient_id ON core.notifications(recipient_id);

-- ============================================================================
-- INDEXES: TRANSACTION SCHEMA
-- ============================================================================
CREATE INDEX idx_transaction_staff_business_id ON transaction.staff(business_id);
CREATE INDEX idx_transaction_staff_user_id ON transaction.staff(user_id);
CREATE INDEX idx_transaction_orders_business_id ON transaction.orders(business_id);
CREATE INDEX idx_transaction_orders_customer_id ON transaction.orders(customer_id);
CREATE INDEX idx_transaction_orders_order_number ON transaction.orders(order_number);
CREATE INDEX idx_transaction_order_items_order_id ON transaction.order_items(order_id);
CREATE INDEX idx_transaction_order_items_product_id ON transaction.order_items(product_id);
CREATE INDEX idx_transaction_order_items_product_variant_id ON transaction.order_items(product_variant_id);
CREATE INDEX idx_transaction_appointments_business_id ON transaction.appointments(business_id);
CREATE INDEX idx_transaction_appointments_order_id ON transaction.appointments(order_id);
CREATE INDEX idx_transaction_appointments_order_item_id ON transaction.appointments(order_item_id);
CREATE INDEX idx_transaction_appointments_customer_id ON transaction.appointments(customer_id);
CREATE INDEX idx_transaction_appointments_staff_id ON transaction.appointments(staff_id);
CREATE INDEX idx_transaction_appointments_start_time ON transaction.appointments(start_time);
CREATE INDEX idx_transaction_payments_business_id ON transaction.payments(business_id);
CREATE INDEX idx_transaction_payments_order_id ON transaction.payments(order_id);
CREATE INDEX idx_transaction_refunds_business_id ON transaction.refunds(business_id);
CREATE INDEX idx_transaction_refunds_order_id ON transaction.refunds(order_id);
