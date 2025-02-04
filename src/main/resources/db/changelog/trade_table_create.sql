create table TRADE (
	TRADE_ID varchar(100) not null,
	VERSION INT not null,
	COUNTER_PARTY_ID varchar(100),
	BOOK_ID varchar(100),
	MATURITY_DATE date not null,
	CREATED_DATE date not null,
	EXPIRED boolean not null,
	primary key (TRADE_ID, VERSION)
)