# Loan API Spec
## Calculate

Endpoint : POST /api/v1/loan/calculate

Request Body :

```json
{
  "vehicleType" : "Mobil",
  "vehicleCondition" : "Baru",
  "vehicleYear" : "2025",
  "totalLoanAmount" : "100000000",
  "loanTenure" : "3",
  "downPayment" : "25000000",
  "isActive" : "N"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "installmentMonthlyAverage": "2441233.33",
    "yearlyInformations": [
      {
        "year": 1,
        "interestRate": 8.0,
        "principalAmount": "75000000.00",
        "totalLoanAmount": "81000000.00",
        "installmentMonthly": "2250000.00",
        "installmentYearly": "27000000.00"
      },
      {
        "year": 2,
        "interestRate": 8.1,
        "principalAmount": "54000000.00",
        "totalLoanAmount": "58374000.00",
        "installmentMonthly": "2432000.00",
        "installmentYearly": "29184000.00"
      },
      {
        "year": 3,
        "interestRate": 8.6,
        "principalAmount": "29190000.00",
        "totalLoanAmount": "31700340.00",
        "installmentMonthly": "2641700.00",
        "installmentYearly": "31700400.00"
      }
    ]
  },
  "errors" : ""
}
```

Response Body (Failed) :
```json
{
  "errors": "Down payment must be at least 35% of loan amount (Rp 35000000.00)"
}
```


