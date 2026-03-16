# The Wild Economy Plugin (TW-ECON-001)

Production-oriented Paper plugin implementing a two-layer economy:

- **Exchange**: strict whitelist, immediate payout for stocking, stock-backed buys only.
- **Marketplace**: player listings with held items, seller payout only on successful sale.

## Setup

1. Build with `./gradlew build` from `the-wild-economy`.
2. Drop jar into `plugins/`.
3. Install Vault + an economy provider.
4. Edit `plugins/TheWildEconomy/config.yml`.
5. Start server; schema is auto-created.

## Commands

### Player
- `/shop exchange`
- `/shop exchange sell <amount>`
- `/shop exchange buy <item> <amount>`
- `/shop market`
- `/shop market list <price>`
- `/shop market buy <listingId>`
- `/shop market withdraw <listingId>`

### Admin
- `/shopadmin reload`
- `/shopadmin exchange stock <item>`

## Data Model

Tables created at startup:
- `exchange_item_definitions`
- `exchange_stock`
- `marketplace_listings`
- `economy_transactions`
- `admin_audit_log`

## v1 Deferred Items

- Auction and bid/offer mechanics
- Physical chest shops/stalls
- Town/region specific taxes or prices
- Recipe-derived automatic pricing
- Item mail/delivery network
- Multi-server sync
- Public projects demand engine (future layering only)

## Notes

- Exchange accepts strict items only (material + metadata policy checks).
- Marketplace serializes exact item payloads for restore on buy/withdraw.
- Listing expiry transitions active listings to `EXPIRED`; claim flow is future extension.
