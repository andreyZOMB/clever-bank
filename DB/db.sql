PGDMP         4                {            clever_bank    15.1    15.1 4    C           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            D           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            E           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            F           1262    16398    clever_bank    DATABASE     m   CREATE DATABASE clever_bank WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'C';
    DROP DATABASE clever_bank;
                postgres    false                        3079    16527 	   uuid-ossp 	   EXTENSION     ?   CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
    DROP EXTENSION "uuid-ossp";
                   false            G           0    0    EXTENSION "uuid-ossp"    COMMENT     W   COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';
                        false    2            �            1259    16434    bank_account    TABLE     �  CREATE TABLE public.bank_account (
    id integer NOT NULL,
    currency smallint NOT NULL,
    value numeric,
    creating_date timestamp without time zone DEFAULT now(),
    end_date timestamp without time zone DEFAULT (now() + '4 years'::interval),
    bank smallint NOT NULL,
    client integer NOT NULL,
    account_number uuid DEFAULT public.uuid_generate_v4(),
    capitalisation boolean DEFAULT false
);
     DROP TABLE public.bank_account;
       public         heap    postgres    false    2            �            1259    16433    bank_account_id_seq    SEQUENCE     �   CREATE SEQUENCE public.bank_account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.bank_account_id_seq;
       public          postgres    false    220            H           0    0    bank_account_id_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE public.bank_account_id_seq OWNED BY public.bank_account.id;
          public          postgres    false    219            �            1259    16418    banks    TABLE     a   CREATE TABLE public.banks (
    id smallint NOT NULL,
    name character varying(20) NOT NULL
);
    DROP TABLE public.banks;
       public         heap    postgres    false            �            1259    16417    banks_id_seq    SEQUENCE     �   CREATE SEQUENCE public.banks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.banks_id_seq;
       public          postgres    false    216            I           0    0    banks_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.banks_id_seq OWNED BY public.banks.id;
          public          postgres    false    215            �            1259    16427    users    TABLE       CREATE TABLE public.users (
    id integer NOT NULL,
    first_name character varying(20),
    last_name character varying(20),
    otchestvo character varying(20),
    login character varying(20),
    hash character varying(400),
    is_admin boolean DEFAULT false NOT NULL
);
    DROP TABLE public.users;
       public         heap    postgres    false            �            1259    16426    clients_id_seq    SEQUENCE     �   CREATE SEQUENCE public.clients_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.clients_id_seq;
       public          postgres    false    218            J           0    0    clients_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.clients_id_seq OWNED BY public.users.id;
          public          postgres    false    217            �            1259    16456    currency    TABLE     R   CREATE TABLE public.currency (
    id smallint NOT NULL,
    name character(3)
);
    DROP TABLE public.currency;
       public         heap    postgres    false            �            1259    16455    currency_id_seq    SEQUENCE     �   CREATE SEQUENCE public.currency_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.currency_id_seq;
       public          postgres    false    224            K           0    0    currency_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.currency_id_seq OWNED BY public.currency.id;
          public          postgres    false    223            �            1259    16511    sessions    TABLE     �   CREATE TABLE public.sessions (
    session_id character(32),
    created_in timestamp without time zone DEFAULT now(),
    last_command character varying(3),
    user_id integer
);
    DROP TABLE public.sessions;
       public         heap    postgres    false            �            1259    16449    transaction    TABLE     �   CREATE TABLE public.transaction (
    id bigint NOT NULL,
    transmitter integer,
    receiver integer,
    value money,
    currency smallint
);
    DROP TABLE public.transaction;
       public         heap    postgres    false            �            1259    16448    transaction_id_seq    SEQUENCE     {   CREATE SEQUENCE public.transaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.transaction_id_seq;
       public          postgres    false    222            L           0    0    transaction_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;
          public          postgres    false    221            �           2604    16437    bank_account id    DEFAULT     r   ALTER TABLE ONLY public.bank_account ALTER COLUMN id SET DEFAULT nextval('public.bank_account_id_seq'::regclass);
 >   ALTER TABLE public.bank_account ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    220    219    220            �           2604    16440    banks id    DEFAULT     d   ALTER TABLE ONLY public.banks ALTER COLUMN id SET DEFAULT nextval('public.banks_id_seq'::regclass);
 7   ALTER TABLE public.banks ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    215    216    216            �           2604    16459    currency id    DEFAULT     j   ALTER TABLE ONLY public.currency ALTER COLUMN id SET DEFAULT nextval('public.currency_id_seq'::regclass);
 :   ALTER TABLE public.currency ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    223    224    224            �           2604    16452    transaction id    DEFAULT     p   ALTER TABLE ONLY public.transaction ALTER COLUMN id SET DEFAULT nextval('public.transaction_id_seq'::regclass);
 =   ALTER TABLE public.transaction ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    222    221    222            �           2604    16430    users id    DEFAULT     f   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.clients_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    217    218            ;          0    16434    bank_account 
   TABLE DATA           �   COPY public.bank_account (id, currency, value, creating_date, end_date, bank, client, account_number, capitalisation) FROM stdin;
    public          postgres    false    220   ;       7          0    16418    banks 
   TABLE DATA           )   COPY public.banks (id, name) FROM stdin;
    public          postgres    false    216   m;       ?          0    16456    currency 
   TABLE DATA           ,   COPY public.currency (id, name) FROM stdin;
    public          postgres    false    224   �;       @          0    16511    sessions 
   TABLE DATA           Q   COPY public.sessions (session_id, created_in, last_command, user_id) FROM stdin;
    public          postgres    false    225   <       =          0    16449    transaction 
   TABLE DATA           Q   COPY public.transaction (id, transmitter, receiver, value, currency) FROM stdin;
    public          postgres    false    222   F=       9          0    16427    users 
   TABLE DATA           \   COPY public.users (id, first_name, last_name, otchestvo, login, hash, is_admin) FROM stdin;
    public          postgres    false    218   c=       M           0    0    bank_account_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.bank_account_id_seq', 6, true);
          public          postgres    false    219            N           0    0    banks_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.banks_id_seq', 12, true);
          public          postgres    false    215            O           0    0    clients_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.clients_id_seq', 2, true);
          public          postgres    false    217            P           0    0    currency_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.currency_id_seq', 3, true);
          public          postgres    false    223            Q           0    0    transaction_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.transaction_id_seq', 1, false);
          public          postgres    false    221            �           2606    16439    bank_account bank_account_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT bank_account_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.bank_account DROP CONSTRAINT bank_account_pkey;
       public            postgres    false    220            �           2606    16425    banks banks_name_key 
   CONSTRAINT     O   ALTER TABLE ONLY public.banks
    ADD CONSTRAINT banks_name_key UNIQUE (name);
 >   ALTER TABLE ONLY public.banks DROP CONSTRAINT banks_name_key;
       public            postgres    false    216            �           2606    16442    banks banks_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.banks
    ADD CONSTRAINT banks_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.banks DROP CONSTRAINT banks_pkey;
       public            postgres    false    216            �           2606    16507    users clients_login_key 
   CONSTRAINT     S   ALTER TABLE ONLY public.users
    ADD CONSTRAINT clients_login_key UNIQUE (login);
 A   ALTER TABLE ONLY public.users DROP CONSTRAINT clients_login_key;
       public            postgres    false    218            �           2606    16432    users clients_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.users
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.users DROP CONSTRAINT clients_pkey;
       public            postgres    false    218            �           2606    16475    currency currency_id_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.currency
    ADD CONSTRAINT currency_id_pkey PRIMARY KEY (id);
 C   ALTER TABLE ONLY public.currency DROP CONSTRAINT currency_id_pkey;
       public            postgres    false    224            �           2606    16454    transaction transaction_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.transaction DROP CONSTRAINT transaction_pkey;
       public            postgres    false    222            �           2606    16464    bank_account bank_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT bank_id_link FOREIGN KEY (bank) REFERENCES public.banks(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 C   ALTER TABLE ONLY public.bank_account DROP CONSTRAINT bank_id_link;
       public          postgres    false    216    3222    220            �           2606    16469    bank_account client_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT client_id_link FOREIGN KEY (client) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 E   ALTER TABLE ONLY public.bank_account DROP CONSTRAINT client_id_link;
       public          postgres    false    218    3226    220            �           2606    16476    bank_account currency_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT currency_id_link FOREIGN KEY (currency) REFERENCES public.currency(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 G   ALTER TABLE ONLY public.bank_account DROP CONSTRAINT currency_id_link;
       public          postgres    false    224    3232    220            �           2606    16481    transaction currency_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT currency_id_link FOREIGN KEY (currency) REFERENCES public.currency(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 F   ALTER TABLE ONLY public.transaction DROP CONSTRAINT currency_id_link;
       public          postgres    false    3232    222    224            �           2606    16496    transaction receiver_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT receiver_id_link FOREIGN KEY (receiver) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 F   ALTER TABLE ONLY public.transaction DROP CONSTRAINT receiver_id_link;
       public          postgres    false    3226    222    218            �           2606    16552    sessions sessions_user_id_fkey    FK CONSTRAINT     }   ALTER TABLE ONLY public.sessions
    ADD CONSTRAINT sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
 H   ALTER TABLE ONLY public.sessions DROP CONSTRAINT sessions_user_id_fkey;
       public          postgres    false    218    225    3226            �           2606    16501    transaction transmitter_id_link    FK CONSTRAINT     �   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transmitter_id_link FOREIGN KEY (transmitter) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 I   ALTER TABLE ONLY public.transaction DROP CONSTRAINT transmitter_id_link;
       public          postgres    false    3226    222    218            ;   W   x�u��� @�3L��
�C4����j���?<�~��f� .{�h�'-��>�?$
.�cfz[P�^)؜<.�zϼ�z�">ǎ�      7   g   x�3�0�¾��[/컰�b���;.���0���@�k�ya:���¦[/콰�b7P�Ѐ���;�J��r:礖�qq�&'�s��qqq #j5�      ?      x�3�t���2�t�2�v����� 6M9      @   #  x�e��Q1��l�)\����-�Q�EX$�f���C zꖬ�e�	a��} M�c��܏
�^ ^���N��(	0����QoЇ̊�v�� �MXu�R>���,��_"b͎�&��2e2�1&!�?�������BRsǆp�V����c�1D異S��Z�����yڛ̀��pɒ� Fmd���� �
� �ob�� Ū��\ե�ʉ9B����Ev߇ Ӗ'̶�ؾ
kxJ���u��$/\YAo�����+L�n�[%�D���w��LD���n�/��xb      =      x������ � �      9   �  x�%RM�A^���'���J����p\8�A�eޜ��F~���������l�������O������_X�o����ߺ�}f���v�������c�>\�w[ti��d!�pA��0Q,��j���2�H3-G,�cM�1�
QŬ�g�!�u�@M�Y�Q�#�/0[��	�G�7��I
"��pA�%�o���P�3�//0�.Y
$�%��.���m(�vۙ�r��A�ɻtP&LBg+tL�TR�.kPTe^c�0�p��i���ۻ�����a�����~+��AE�P���(��w���!"c�Z SM�hKpF�.-9�#��&N!�3+=�-�.V�3�QƁ�:��it/��`������_�&c^X^m>��8c�'�0#J���:B�G �Cz�� Kթ�F��Sk����_iI�+k*�TNW'>��g�,|Us{��}s�\�e��b     