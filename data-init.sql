--
-- TABLAS
--

create table public.bodega
(
    id        uuid default uuid_generate_v4() not null
        constraint bodega_id_pk
            primary key,
    nombre    text                            not null,
    direccion text                            not null,
    ciudad    text                            not null
);

alter table public.bodega
    owner to postgres;

grant delete, insert, references, select, trigger, truncate, update on public.bodega to anon;

grant delete, insert, references, select, trigger, truncate, update on public.bodega to authenticated;

grant delete, insert, references, select, trigger, truncate, update on public.bodega to service_role;

create table public.categoria
(
    id     uuid default extensions.uuid_generate_v4() not null
        constraint categoria_id_pk
            primary key,
    nombre text                                       not null
);

alter table public.categoria
    owner to postgres;

create table public.producto
(
    id           uuid default extensions.uuid_generate_v4() not null
        constraint producto_id_pk
            primary key,
    precio       bigint                                     not null,
    nombre       text                                       not null,
    marca        text                                       not null,
    categoria_id uuid                                       not null
        constraint producto_categoria_id_fk
            references public.categoria
);

alter table public.producto
    owner to postgres;

grant delete, insert, references, select, trigger, truncate, update on public.producto to anon;

grant delete, insert, references, select, trigger, truncate, update on public.producto to authenticated;

grant delete, insert, references, select, trigger, truncate, update on public.producto to service_role;

create table public.producto_bodega
(
    producto_id uuid not null
        constraint producto_bodega_producto_id_fk
            references public.producto
            on delete cascade,
    bodega_id   uuid not null
        constraint producto_bodega_bodega_id_fk
            references public.bodega,
    constraint producto_bodega_id_pk
        primary key (bodega_id, producto_id)
);

alter table public.producto_bodega
    owner to postgres;

grant delete, insert, references, select, trigger, truncate, update on public.producto_bodega to anon;

grant delete, insert, references, select, trigger, truncate, update on public.producto_bodega to authenticated;

grant delete, insert, references, select, trigger, truncate, update on public.producto_bodega to service_role;

grant delete, insert, references, select, trigger, truncate, update on public.categoria to anon;

grant delete, insert, references, select, trigger, truncate, update on public.categoria to authenticated;

grant delete, insert, references, select, trigger, truncate, update on public.categoria to service_role;

--
-- CATEGORÍAS
--
INSERT INTO public.categoria (id, nombre)
VALUES ('d2608c1e-5f1a-4e4b-bd71-3f6397a1c2a1', 'Bebidas'),
       ('b385b7f2-a2d9-4f9f-bccc-e27f54f3a2e2', 'Lácteos'),
       ('ac473d3f-2f61-4d7f-8b3e-5a1e2f1c4567', 'Pastas'),
       ('f123ec8b-9f34-4c21-9274-a0b3cde5d6f7', 'Salsas y Conservas'),
       ('e9b7124a-6cd8-4f89-8d2e-34a567bc9d01', 'Desayuno y Dulces');

--
-- BODEGAS
--
INSERT INTO public.bodega (id, nombre, direccion, ciudad)
VALUES ('5cba48d2-7a66-4a8d-bc63-1e2f3a4b5c6d', 'CD Pudahuel',
        'Av. Américo Vespucio 1309, Parque ENEA', 'Santiago'),
       ('6dcb7901-3f4e-41a2-9d07-2e3f4b5c6d7e', 'CD Valparaíso', 'Camino La Pólvora 1000, Placilla',
        'Valparaíso'),
       ('7eab8102-4b5c-41a3-8f18-3f4c5d6e7f8a', 'CD Concepción', 'Av. Costanera 500, Talcahuano',
        'Concepción'),
       ('8fab9203-5c6d-41a4-8a29-4f5d6e7f8a9b', 'CD Antofagasta', 'Ruta 5 Km 1360, Sector La Negra',
        'Antofagasta');

--
-- PRODUCTOS
--
INSERT INTO public.producto (id, precio, nombre, marca, categoria_id)
VALUES (extensions.uuid_generate_v4(), 1190, 'Spaghetti 400g', 'Carozzi',
        'ac473d3f-2f61-4d7f-8b3e-5a1e2f1c4567'),
       (extensions.uuid_generate_v4(), 1290, 'Mostachol 400g', 'Lucchetti',
        'ac473d3f-2f61-4d7f-8b3e-5a1e2f1c4567'),
       (extensions.uuid_generate_v4(), 990, 'Salsa de Tomate 200g', 'Carozzi',
        'f123ec8b-9f34-4c21-9274-a0b3cde5d6f7'),
       (extensions.uuid_generate_v4(), 1890, 'Mermelada Damasco 250g', 'Watt''s',
        'f123ec8b-9f34-4c21-9274-a0b3cde5d6f7'),
       (extensions.uuid_generate_v4(), 1390, 'Leche Entera 1L', 'Soprole',
        'b385b7f2-a2d9-4f9f-bccc-e27f54f3a2e2'),
       (extensions.uuid_generate_v4(), 1290, 'Bilz 1.5L', 'CCU',
        'd2608c1e-5f1a-4e4b-bd71-3f6397a1c2a1'),
       (extensions.uuid_generate_v4(), 1290, 'Pap 1.5L', 'CCU',
        'd2608c1e-5f1a-4e4b-bd71-3f6397a1c2a1'),
       (extensions.uuid_generate_v4(), 1290, 'Kem Piña 1.5L', 'CCU',
        'd2608c1e-5f1a-4e4b-bd71-3f6397a1c2a1'),
       (extensions.uuid_generate_v4(), 2590, 'Néctar Durazno 1.5L', 'Watt''s',
        'e9b7124a-6cd8-4f89-8d2e-34a567bc9d01'),
       (extensions.uuid_generate_v4(), 2190, 'Jugo Naranja 1L', 'Watt''s',
        'e9b7124a-6cd8-4f89-8d2e-34a567bc9d01');

--
-- PRODUCTO_BODEGA
--
INSERT INTO public.producto_bodega (bodega_id, producto_id)
SELECT b.id AS bodega_id, p.id AS producto_id
FROM public.bodega b
         JOIN public.producto p
              ON (
                  (b.ciudad = 'Santiago' AND p.nombre IN ('Spaghetti 400g', 'Mostachol 400g',
                                                          'Salsa de Tomate 200g',
                                                          'Mermelada Damasco 250g',
                                                          'Leche Entera 1L', 'Bilz 1.5L',
                                                          'Pap 1.5L', 'Kem Piña 1.5L',
                                                          'Néctar Durazno 1.5L', 'Jugo Naranja 1L'))
                      OR (b.ciudad = 'Valparaíso' AND p.nombre IN
                                                      ('Spaghetti 400g', 'Mostachol 400g',
                                                       'Bilz 1.5L', 'Pap 1.5L', 'Kem Piña 1.5L'))
                      OR (b.ciudad = 'Concepción' AND p.nombre IN ('Salsa de Tomate 200g',
                                                                   'Mermelada Damasco 250g',
                                                                   'Leche Entera 1L',
                                                                   'Néctar Durazno 1.5L'))
                      OR (b.ciudad = 'Antofagasta' AND p.nombre IN
                                                       ('Spaghetti 400g', 'Bilz 1.5L', 'Pap 1.5L',
                                                        'Néctar Durazno 1.5L'))
                  );
