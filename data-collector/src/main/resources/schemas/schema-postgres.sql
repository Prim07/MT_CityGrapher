CREATE TABLE IF NOT EXISTS public.city_data
(
    id        SERIAL PRIMARY KEY,
    city_name VARCHAR(100),
    city_data BYTEA
);