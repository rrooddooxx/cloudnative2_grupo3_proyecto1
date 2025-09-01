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

