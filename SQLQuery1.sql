-- Create and use database
USE CreditUnionSystemDB;
GO

-- Delete existing data
DELETE FROM Transactions;
DELETE FROM Accounts;
DELETE FROM Members;
DELETE FROM Staff;
GO

-- Insert Staff
INSERT INTO Staff (username, password, role, full_name) 
VALUES ('admin', 'admin123', 'Admin', 'System Administrator');
GO

-- Insert Members
INSERT INTO Members (full_name, phone, email, address, username, password) 
VALUES 
('John Doe', '1234567890', 'john@email.com', '123 Main St', 'john', 'member123'),
('Jane Smith', '0987654321', 'jane@email.com', '456 Oak Ave', 'jane', 'member123');
GO

-- Insert Accounts
INSERT INTO Accounts (member_id, account_number, account_type, balance) 
VALUES 
((SELECT member_id FROM Members WHERE username = 'john'), 'ACC1001', 'Savings', 1500.00),
((SELECT member_id FROM Members WHERE username = 'jane'), 'ACC1002', 'Savings', 2500.00);
GO

-- Insert Transactions for John
INSERT INTO Transactions (account_number, transaction_type, amount, description, performed_by) 
VALUES 
('ACC1001', 'Deposit', 1000.00, 'Initial Deposit', 'system'),
('ACC1001', 'Deposit', 500.00, 'Salary Deposit', 'system');
GO

-- Insert Transactions for Jane
INSERT INTO Transactions (account_number, transaction_type, amount, description, performed_by) 
VALUES 
('ACC1002', 'Deposit', 2000.00, 'Initial Deposit', 'system'),
('ACC1002', 'Deposit', 500.00, 'Freelance Payment', 'system');
GO

-- Verify
SELECT 'Staff' as TableName, COUNT(*) as Count FROM Staff
UNION ALL
SELECT 'Members', COUNT(*) FROM Members
UNION ALL
SELECT 'Accounts', COUNT(*) FROM Accounts
UNION ALL
SELECT 'Transactions', COUNT(*) FROM Transactions;
GO

-- Show all data
SELECT * FROM Staff;
SELECT * FROM Members;
SELECT * FROM Accounts;
SELECT * FROM Transactions ORDER BY transaction_date DESC;
GO