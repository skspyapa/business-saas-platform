-- Flyway Migration V2: Insert Initial Pricing Plans

INSERT INTO tenant.pricing_plans (
    plan_type, display_name, description, monthly_price, max_users, max_storage_gb, features, 
    created_at, updated_at, created_by, updated_by
) VALUES 
(
    'FREE', 
    'Free Starter', 
    'Perfect for individuals and small startups testing the waters.', 
    0.00, 1, 5, 
    '{"appointments": true, "inventory": false, "loyalty": false}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'BASIC', 
    'Basic Professional', 
    'Core tools for growing small businesses.', 
    29.99, 5, 20, 
    '{"appointments": true, "inventory": true, "loyalty": false, "custom_domain": true}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'PREMIUM', 
    'Premium Business', 
    'Advanced features for established organizations.', 
    79.99, 20, 100, 
    '{"appointments": true, "inventory": true, "loyalty": true, "advanced_reporting": true, "custom_domain": true}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'ENTERPRISE', 
    'Enterprise Scale', 
    'Unlimited scale and dedicated support for maximal operations.', 
    249.99, 99999, 1000, 
    '{"appointments": true, "inventory": true, "loyalty": true, "advanced_reporting": true, "api_access": true, "dedicated_support": true, "custom_domain": true}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
)
ON CONFLICT (plan_type) DO NOTHING;
