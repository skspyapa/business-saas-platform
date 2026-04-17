-- Flyway Migration V1: Create all database tables and schemas
-- Generated from entity analysis of business-saas-platform modules
-- Schemas: tenant, catalog, core, transaction

-- Create UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- TENANT SCHEMA: Users, Businesses, Permissions, Subscriptions
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS tenant;

-- Entity: User
-- Auditab Field: No parent entities
CREATE TABLE IF NOT EXISTS tenant.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Entity: Permission
-- Parent: None (reference table)
CREATE TABLE IF NOT EXISTS tenant.permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(255),
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Entity: PricingPlan
-- Parent: None (reference table)
CREATE TABLE IF NOT EXISTS tenant.pricing_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_type VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    monthly_price NUMERIC(19, 2) NOT NULL,
    max_users INTEGER NOT NULL,
    max_storage_gb INTEGER NOT NULL,
    features JSONB,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Entity: Business
-- Parent: tenant.users (owner)
CREATE TABLE IF NOT EXISTS tenant.businesses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    business_type VARCHAR(255) NOT NULL,
    description TEXT,
    subdomain VARCHAR(255) NOT NULL UNIQUE,
    logo_url VARCHAR(1024),
    website_url VARCHAR(1024),
    country VARCHAR(2),
    city VARCHAR(255),
    address TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_business_owner FOREIGN KEY (owner_id) REFERENCES tenant.users(id) ON DELETE RESTRICT
);

-- Entity: BusinessUserRole
-- Parent: tenant.businesses, tenant.users
-- Note: Permissions are managed via role_permissions join table (NOT embedded as JSONB)
CREATE TABLE IF NOT EXISTS tenant.business_user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_bur_business FOREIGN KEY (business_id) REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_bur_user FOREIGN KEY (user_id) REFERENCES tenant.users(id) ON DELETE CASCADE,
    CONSTRAINT uk_business_user_role UNIQUE (business_id, user_id)
);

-- Entity: RolePermission
-- Parent: tenant.business_user_roles, tenant.permissions
-- Join table: BusinessUserRole -> Permission (many-to-many with granted flag)
CREATE TABLE IF NOT EXISTS tenant.role_permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_user_role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    granted BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_rp_role FOREIGN KEY (business_user_role_id) REFERENCES tenant.business_user_roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES tenant.permissions(id) ON DELETE RESTRICT,
    CONSTRAINT uk_role_permission UNIQUE (business_user_role_id, permission_id)
);

-- Entity: Subscription
-- Parent: tenant.businesses, tenant.pricing_plans
CREATE TABLE IF NOT EXISTS tenant.subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    pricing_plan_id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_subscription_business FOREIGN KEY (business_id) REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (pricing_plan_id) REFERENCES tenant.pricing_plans(id) ON DELETE RESTRICT
);

-- Entity: BusinessSettings
-- Parent: tenant.businesses
CREATE TABLE IF NOT EXISTS tenant.business_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    settings JSONB NOT NULL,
    description TEXT,
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_settings_business FOREIGN KEY (business_id) REFERENCES tenant.businesses(id) ON DELETE CASCADE,
    CONSTRAINT uk_business_settings UNIQUE (business_id)
);

-- ============================================================================
-- CORE SCHEMA: Customers, Notifications, Inventory
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS core;

-- Entity: Customer
-- Parent: None (references tenant.users via UUID, but not a FK)
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS core.customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    user_id UUID,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    loyalty_points INTEGER NOT NULL DEFAULT 0,
    total_spent NUMERIC(19, 2) NOT NULL DEFAULT 0,
    last_purchase TIMESTAMP,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Entity: Notification
-- Parent: None (references business and recipient via UUID, but not direct FKs)
-- Includes: read_at timestamp for audit
CREATE TABLE IF NOT EXISTS core.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data TEXT,
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Entity: Inventory
-- Parent: None direct (references product_variants via UUID, not FK)
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS core.inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    product_variant_id UUID NOT NULL,
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    quantity_damaged INTEGER NOT NULL DEFAULT 0,
    last_stock_check TIMESTAMP,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uk_inventory_variant UNIQUE (business_id, product_variant_id)
);

-- ============================================================================
-- CATALOG SCHEMA: Categories, Products, ProductVariants, Reviews
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS catalog;

-- Entity: Category
-- Parent: None (Tenant-scoped via businessId)
CREATE TABLE IF NOT EXISTS catalog.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    color VARCHAR(7),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uk_category_name UNIQUE (business_id, name)
);

-- Entity: Product
-- Parent: catalog.categories (optional)
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS catalog.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    category_id UUID,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    product_type VARCHAR(50) NOT NULL,
    base_price NUMERIC(19, 2) NOT NULL,
    cost_price NUMERIC(19, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    images TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_available BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES catalog.categories(id) ON DELETE SET NULL
);

-- Entity: ProductVariant
-- Parent: catalog.products
CREATE TABLE IF NOT EXISTS catalog.product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    price_modifier NUMERIC(19, 2),
    cost_modifier NUMERIC(19, 2),
    stock_quantity INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_product_variant_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE CASCADE
);

-- Entity: Review
-- Parent: catalog.products (optional), references Customer/Staff via UUID (no FK)
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS catalog.reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    product_id UUID,
    customer_id UUID NOT NULL,
    staff_id UUID,
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN NOT NULL DEFAULT false,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE CASCADE
);

-- ============================================================================
-- TRANSACTION SCHEMA: Orders, OrderItems, Payments, Refunds, Appointments, Staff
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS transaction;

-- Entity: Staff
-- Parent: tenant.users (optional - separate staff record)
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS transaction.staff (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    user_id UUID,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    title VARCHAR(255),
    bio TEXT,
    avatar_url VARCHAR(1024),
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_available BOOLEAN NOT NULL DEFAULT true,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES tenant.users(id) ON DELETE SET NULL
);

-- Entity: Order
-- Parent: core.customers
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS transaction.orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    order_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    subtotal NUMERIC(19, 2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method VARCHAR(100),
    payment_status VARCHAR(50),
    shipping_address TEXT,
    notes TEXT,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES core.customers(id) ON DELETE RESTRICT
);

-- Entity: OrderLine (named as order_items in DB)
-- Parent: transaction.orders, catalog.products, catalog.product_variants
CREATE TABLE IF NOT EXISTS transaction.order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_variant_id UUID,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(19, 2) NOT NULL,
    line_total NUMERIC(19, 2) NOT NULL,
    notes TEXT,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES transaction.orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_item_variant FOREIGN KEY (product_variant_id) REFERENCES catalog.product_variants(id) ON DELETE SET NULL
);

-- Entity: Payment
-- Parent: transaction.orders
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS transaction.payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method VARCHAR(100) NOT NULL,
    payment_gateway VARCHAR(100),
    transaction_id VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    receipt_url VARCHAR(1024),
    failure_reason TEXT,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES transaction.orders(id) ON DELETE RESTRICT
);

-- Entity: Refund
-- Parent: transaction.orders, transaction.payments, transaction.staff
CREATE TABLE IF NOT EXISTS transaction.refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    order_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    processed_by UUID,
    amount NUMERIC(19, 2) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_refund_order FOREIGN KEY (order_id) REFERENCES transaction.orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES transaction.payments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_refund_staff FOREIGN KEY (processed_by) REFERENCES transaction.staff(id) ON DELETE SET NULL
);

-- Entity: Appointment
-- Parent: transaction.orders (optional), transaction.order_items (optional), core.customers, transaction.staff, catalog.products, catalog.product_variants
-- Tenant-scoped: businessId
CREATE TABLE IF NOT EXISTS transaction.appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL,
    order_id UUID,
    order_item_id UUID,
    customer_id UUID NOT NULL,
    staff_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_variant_id UUID,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    location VARCHAR(500),
    notes TEXT,
    customer_notes TEXT,
    reminder_sent BOOLEAN NOT NULL DEFAULT false,
    -- Base Entity Audit Fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_appointment_order FOREIGN KEY (order_id) REFERENCES transaction.orders(id) ON DELETE SET NULL,
    CONSTRAINT fk_appointment_order_item FOREIGN KEY (order_item_id) REFERENCES transaction.order_items(id) ON DELETE SET NULL,
    CONSTRAINT fk_appointment_customer FOREIGN KEY (customer_id) REFERENCES core.customers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_staff FOREIGN KEY (staff_id) REFERENCES transaction.staff(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_variant FOREIGN KEY (product_variant_id) REFERENCES catalog.product_variants(id) ON DELETE SET NULL
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- ============================================================================

-- Tenant Schema Indexes
CREATE INDEX idx_users_email ON tenant.users(email);
CREATE INDEX idx_businesses_owner ON tenant.businesses(owner_id);
CREATE INDEX idx_businesses_subdomain ON tenant.businesses(subdomain);
CREATE INDEX idx_businesses_is_active ON tenant.businesses(is_active);
CREATE INDEX idx_bur_business ON tenant.business_user_roles(business_id);
CREATE INDEX idx_bur_user ON tenant.business_user_roles(user_id);
CREATE INDEX idx_bur_is_active ON tenant.business_user_roles(is_active);
CREATE INDEX idx_rp_role ON tenant.role_permissions(business_user_role_id);
CREATE INDEX idx_rp_permission ON tenant.role_permissions(permission_id);
CREATE INDEX idx_subscription_business ON tenant.subscriptions(business_id);
CREATE INDEX idx_subscription_plan ON tenant.subscriptions(pricing_plan_id);
CREATE INDEX idx_subscription_is_active ON tenant.subscriptions(is_active);
CREATE INDEX idx_business_settings_business ON tenant.business_settings(business_id);

-- Core Schema Indexes
CREATE INDEX idx_customers_business ON core.customers(business_id);
CREATE INDEX idx_customers_user ON core.customers(user_id);
CREATE INDEX idx_customers_email ON core.customers(email);
CREATE INDEX idx_customers_loyalty ON core.customers(loyalty_points);
CREATE INDEX idx_notifications_business ON core.notifications(business_id);
CREATE INDEX idx_notifications_recipient ON core.notifications(recipient_id);
CREATE INDEX idx_notifications_is_read ON core.notifications(is_read);
CREATE INDEX idx_notifications_type ON core.notifications(notification_type);
CREATE INDEX idx_inventory_business ON core.inventory(business_id);
CREATE INDEX idx_inventory_variant ON core.inventory(product_variant_id);

-- Catalog Schema Indexes
CREATE INDEX idx_categories_business ON catalog.categories(business_id);
CREATE INDEX idx_categories_is_active ON catalog.categories(is_active);
CREATE INDEX idx_products_business ON catalog.products(business_id);
CREATE INDEX idx_products_category ON catalog.products(category_id);
CREATE INDEX idx_products_is_active ON catalog.products(is_active);
CREATE INDEX idx_products_type ON catalog.products(product_type);
CREATE INDEX idx_product_variants_product ON catalog.product_variants(product_id);
CREATE INDEX idx_product_variants_sku ON catalog.product_variants(sku);
CREATE INDEX idx_product_variants_is_active ON catalog.product_variants(is_active);
CREATE INDEX idx_reviews_business ON catalog.reviews(business_id);
CREATE INDEX idx_reviews_product ON catalog.reviews(product_id);
CREATE INDEX idx_reviews_customer ON catalog.reviews(customer_id);
CREATE INDEX idx_reviews_status ON catalog.reviews(status);

-- Transaction Schema Indexes
CREATE INDEX idx_staff_business ON transaction.staff(business_id);
CREATE INDEX idx_staff_user ON transaction.staff(user_id);
CREATE INDEX idx_staff_is_active ON transaction.staff(is_active);
CREATE INDEX idx_orders_business ON transaction.orders(business_id);
CREATE INDEX idx_orders_customer ON transaction.orders(customer_id);
CREATE INDEX idx_orders_status ON transaction.orders(status);
CREATE INDEX idx_orders_order_number ON transaction.orders(order_number);
CREATE INDEX idx_orders_payment_status ON transaction.orders(payment_status);
CREATE INDEX idx_orders_created_at ON transaction.orders(created_at);
CREATE INDEX idx_order_items_order ON transaction.order_items(order_id);
CREATE INDEX idx_order_items_product ON transaction.order_items(product_id);
CREATE INDEX idx_order_items_variant ON transaction.order_items(product_variant_id);
CREATE INDEX idx_payments_business ON transaction.payments(business_id);
CREATE INDEX idx_payments_order ON transaction.payments(order_id);
CREATE INDEX idx_payments_customer ON transaction.payments(customer_id);
CREATE INDEX idx_payments_status ON transaction.payments(status);
CREATE INDEX idx_refunds_business ON transaction.refunds(business_id);
CREATE INDEX idx_refunds_order ON transaction.refunds(order_id);
CREATE INDEX idx_refunds_payment ON transaction.refunds(payment_id);
CREATE INDEX idx_refunds_status ON transaction.refunds(status);
CREATE INDEX idx_appointments_business ON transaction.appointments(business_id);
CREATE INDEX idx_appointments_customer ON transaction.appointments(customer_id);
CREATE INDEX idx_appointments_staff ON transaction.appointments(staff_id);
CREATE INDEX idx_appointments_status ON transaction.appointments(status);
CREATE INDEX idx_appointments_start_time ON transaction.appointments(start_time);
CREATE INDEX idx_appointments_end_time ON transaction.appointments(end_time);
CREATE INDEX idx_appointments_order ON transaction.appointments(order_id);

-- ============================================================================
-- SOFT DELETE INDEXES (for common queries with is_deleted filter)
-- ============================================================================

CREATE INDEX idx_users_soft_delete ON tenant.users(email, is_deleted);
CREATE INDEX idx_businesses_soft_delete ON tenant.businesses(subdomain, is_deleted);
CREATE INDEX idx_products_soft_delete ON catalog.products(business_id, is_deleted);
CREATE INDEX idx_orders_soft_delete ON transaction.orders(business_id, status, is_deleted);
CREATE INDEX idx_appointments_soft_delete ON transaction.appointments(business_id, status, is_deleted);

-- ============================================================================
-- ENTITY MAPPING REFERENCE
-- ============================================================================
-- 
-- TENANT SCHEMA (04 tables):
--   1. users              <- User entity
--   2. permissions        <- Permission entity
--   3. pricing_plans      <- PricingPlan entity
--   4. businesses         <- Business entity
--   5. business_user_roles <- BusinessUserRole entity
--   6. role_permissions   <- RolePermission entity (join table)
--   7. subscriptions      <- Subscription entity
--   8. business_settings  <- BusinessSettings entity
--
-- CORE SCHEMA (03 tables):
--   9. customers          <- Customer entity
--  10. notifications      <- Notification entity (with read_at timestamp)
--  11. inventory          <- Inventory entity
--
-- CATALOG SCHEMA (04 tables):
--  12. categories         <- Category entity
--  13. products           <- Product entity
--  14. product_variants   <- ProductVariant entity
--  15. reviews            <- Review entity
--
-- TRANSACTION SCHEMA (06 tables):
--  16. staff              <- Staff entity
--  17. orders             <- Order entity
--  18. order_items        <- OrderLine entity
--  19. payments           <- Payment entity
--  20. refunds            <- Refund entity
--  21. appointments       <- Appointment entity
--
-- TOTAL: 21 entities, 4 schemas, 80+ indexes
--
-- KEY DESIGN DECISIONS:
-- - All tables include BaseEntity fields: id, created_at, updated_at, created_by, updated_by, is_deleted
-- - BusinessUserRole: permissions stored via role_permissions join table (not JSONB)
-- - Notification: includes read_at timestamp for audit trail
-- - Soft deletions: is_deleted flag + indexes for efficient querying
-- - Foreign key constraints with appropriate ON DELETE rules (CASCADE, RESTRICT, SET NULL)
-- - Tenant-scoped tables include business_id for multi-tenancy
-- - UUID primary keys with uuid_generate_v4() default
-- - JSONB support for features (pricing_plans) and settings (business_settings)
-- ============================================================================
