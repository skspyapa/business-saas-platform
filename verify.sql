-- Schema Overview
SELECT COUNT(*) as schema_count FROM information_schema.schemata 
WHERE schema_name NOT IN ('pg_catalog', 'information_schema', 'pg_toast', 'public');

-- Tables by Schema
SELECT schemaname, COUNT(*) as table_count 
FROM pg_tables 
WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'pg_toast') 
GROUP BY schemaname 
ORDER BY schemaname;

-- Indexes Count
SELECT COUNT(*) as total_indexes FROM pg_indexes 
WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'pg_toast', 'public');

-- Foreign Keys Count
SELECT COUNT(*) as total_fk_constraints FROM information_schema.table_constraints 
WHERE constraint_type='FOREIGN KEY' 
AND table_schema NOT IN ('pg_catalog', 'information_schema');
