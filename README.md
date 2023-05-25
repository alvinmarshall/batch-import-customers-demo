# Batch Import Customers

This project utilizes Spring Batch to import customers from
a batch zip file, extract and save all customers along with their
relationships to a database

## UseCase

Receive a batch zip file which includes:

- Individual: 'customers_ind.csv'
- Organization: 'customers_org.csv'
- Address: 'addresses.csv'
- BusinessUnit: 'business_units.csv'
- Document: 'documents.csv'
- Account: 'accounts.csv'
- ProductOffered: 'products_offered.csv'
- Relationship: 'relationships.csv'
- MarketServed: 'markets_served.csv'
- CountryOfOperation: 'countries_of_operation.csv'
- BeneficialOwner: 'beneficial_owners.csv'

Parse these csv and generate a relational mapping of these
csv into a relational database.
Make sure all relationships are mapped correctly into the database
