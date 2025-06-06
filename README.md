# 🏦 Java Banking Application

A command-line Java-based banking application that allows users to securely manage their financial accounts. Features include account creation, deposits, withdrawals, money transfers, issuing charges, loan handling, password recovery with 2FA, and administrative tools such as transaction recall and loan approval.

## 📦 Features

### ✅ User Functions

* Create a new user account
* Login & Logout
* View account number and balance
* Deposit / Withdraw money
* Transfer funds to other users (by account number)
* Issue and receive charges
* Request and repay loans
* Enable/Disable 2FA (TOTP-based) for password recovery
* Reset password via 2FA
* Change username or password
* Print account statement

### 🔐 Security

* SHA-256 password hashing
* TOTP-based 2FA for secure account recovery
* QR code generation for 2FA setup

### 👨‍💼 Admin Functions

* View all transactions
* Recall invalid or duplicate transactions
* Review and approve or reject loan requests

> **Note:** An administrator account is pre-initialized with:
>
> * **Username:** `admin`
> * **Password:** (leave blank — just press Enter)
>
> You can log in using these credentials from the login screen to access admin-only features.

### 🧪 Testing

* JUnit tests included for: `User`, `Transaction`, `Menu`, `Loan`, `Option`, `Database`, and `FileSystem`.

## 🗂️ Project Layout

Source code is under `src/banking/`, tests under `src/tests/`, and dependencies in `lib/`. Run the app using `run.sh`.

## 🔧 Dependencies

* Java 17+
* JUnit 5
* Nayuki QR Code Generator
* Apache Commons Codec (`Base32` for 2FA)

## 🚀 Running the Application

```bash
git clone https://github.com/HaotingShen/banking-cli-app.git
chmod +x run.sh
./run.sh
```

This script will:

1. Clean and recompile the source files.
2. Run all unit tests using JUnit 5.
3. Launch the CLI banking interface.


## 📂 Data Persistence

* Users and transaction data are stored in `userMap.ser` and `transactionMap.ser`.
* These are automatically updated via `FileSystem.java` during transactions or account changes.

## 🔒 2FA Setup

When enabling 2FA:

* A secret key will be shown and a QR code is printed to the terminal.
* Scan the QR code using Google Authenticator, Authy, Duo Mobile, or any similar app.
* Once the account is added, you will be able to use the `Reset Password` option on the login page to reset your password without logging in.

## ✍️ Authors

Lead Developer: Haoting Shen

Contributors: Ica Chen, Amuka Shrestha, Tariq Jassim