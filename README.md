#### CathCoindesk

#### 撰寫時API呼叫無效, 全用mock資料做為source
#### 1. 呼叫 Coindesk API 並提供原始資料 `/coindesk/raw`
#### 2. 轉換 Coindesk 資料並回傳 `/coindesk/converted`
#### 3. 單元測試: CoindeskCurrencyTest, SvcUnitTest
#### 4. sch(10秒更新一次): CoindeskScheduler
#### 5. 增刪改查db在CurrencyController

#### 加分題: Dockerfile, 

#### 建表SQL
```
CREATE TABLE currency (
code VARCHAR(10) PRIMARY KEY,
name_zh VARCHAR(100) NOT NULL,
rate DOUBLE
);
```