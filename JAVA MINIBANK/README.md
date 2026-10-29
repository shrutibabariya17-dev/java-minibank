# MiniBank — Java OOP Practicals Capstone

A complete Java console banking application built across 14 practicals covering
Object-Oriented Programming concepts from basic inheritance to generics, reflection,
concurrency, persistence and integration.

## Java Version

OpenJDK 25.0.3 (Android Studio bundled JBR)

    javac : C:\Program Files\Android\Android Studio\jbr\bin\javac.exe
    java  : C:\Program Files\Android\Android Studio\jbr\bin\java.exe
    jar   : C:\Program Files\Android\Android Studio\jbr\bin\jar.exe

## Folder Structure

    MiniBank-JAVA/
    +-- *.java            (all source files in root, no subfolders)
    +-- Manifest.txt
    +-- lib/
    |   +-- junit-4.13.2.jar
    |   +-- hamcrest-core-1.3.jar
    +-- out/              (compiled .class files)
    +-- dist/
    |   +-- MiniBank.jar  (runnable JAR)
    +-- data/
        +-- accounts.dat
        +-- logs/transactions.log
        +-- report/eod-report.txt

## Compile Application

    $javac = "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
    & $javac -d out *.java  # excludes BankServiceTest.java if desired

## Compile Tests

    $cp = "out;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar"
    & $javac -cp $cp -d out BankServiceTest.java

## Run from classes

    $java = "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
    & $java -cp out MiniBank

## Run from JAR

    & $java -jar dist\MiniBank.jar

## Run JUnit Tests

    $cp = "out;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar"
    & $java -cp $cp org.junit.runner.JUnitCore BankServiceTest

Expected: OK (4 tests)

## Build JAR

    $jar = "C:\Program Files\Android\Android Studio\jbr\bin\jar.exe"
    & $jar --create --file dist\MiniBank.jar --manifest Manifest.txt -C out .

## Menu Options (Practical 14)

    1  Open Account   (SAVINGS / CURRENT / FIXED)
    2  Deposit
    3  Withdraw
    4  Transfer
    5  View Account Statement
    6  List All Accounts
    7  Generate End-of-Day Report
    8  Batch Process Transactions
    9  Save Accounts
    10 Search by Owner Name
    0  Exit (auto-saves accounts)

## Key Classes

    Account          Abstract base with deposit/withdraw/transfer/safeTransfer
    BankService      Service layer connecting repository, logging, exceptions
    Repository<T>    Generic HashMap store using @Id field via reflection
    AccountStore     Practical 12: HashMap, ArrayList history, sortedByBalance
    TransactionProcessor  ExecutorService + ConcurrentHashMap shared accounts
    StatePersister   ObjectOutputStream/ObjectInputStream serialization
    TransactionLog   Files.write APPEND, UTF-8, CSV log format
    ReportGenerator  DirectoryStream walk, BufferedReader, readAttributes totals
    StatementFormatter  Readable account statement builder
    BankServiceTest  JUnit 4: deposit, withdrawal limit, concurrency, persistence
