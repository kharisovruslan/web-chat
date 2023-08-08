alter table message alter column message_text type character varying(5242880);
alter table chat_users add column clientVersion character varying(255);