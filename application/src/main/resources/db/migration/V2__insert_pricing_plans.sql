-- Flyway Migration V2: Insert Initial Pricing Plans

INSERT INTO tenant.pricing_plans (
    plan_type, display_name, description, monthly_price, max_users, features, 
    created_at, updated_at, created_by, updated_by
) VALUES 
(
    'FREE', 
    'Free Starter', 
    'Perfect for individuals and small startups testing the waters.', 
    0.00, 1, 
    '{"appointments": true, "inventory": false, "loyalty_program": false, "advanced_reporting": false, "api_access": false, "custom_domain": false, "white_label": false, "support_level": "community", "max_customers": 100, "staff_accounts": 1}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'BASIC', 
    'Basic Professional', 
    'Core tools for growing small businesses.', 
    29.99, 5, 
    '{"appointments": true, "inventory": true, "loyalty_program": false, "advanced_reporting": false, "api_access": false, "custom_domain": true, "white_label": false, "support_level": "email", "max_customers": 1000, "staff_accounts": 5}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'PREMIUM', 
    'Premium Business', 
    'Advanced features for established organizations.', 
    79.99, 20, 
    '{"appointments": true, "inventory": true, "loyalty_program": true, "advanced_reporting": true, "api_access": false, "custom_domain": true, "white_label": false, "support_level": "priority", "max_customers": 10000, "staff_accounts": 20}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
),
(
    'ENTERPRISE', 
    'Enterprise Scale', 
    'Unlimited scale and dedicated support for maximal operations.', 
    249.99, 99999, 
    '{"appointments": true, "inventory": true, "loyalty_program": true, "advanced_reporting": true, "api_access": true, "custom_domain": true, "white_label": true, "support_level": "dedicated", "max_customers": -1, "staff_accounts": -1}'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM_FLYWAY', 'SYSTEM_FLYWAY'
)
ON CONFLICT (plan_type) DO NOTHING;
