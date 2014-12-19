package it.unimol.my.taxes;

/**
 * Questo Bean rappresenta una singola tassa universitaria
 *
 * @author Carlo Branca
 */
public class Tax {

    private String billId;
    private String bullettinCode;
    private String year;
    private String description;
    private String expiringDate;
    private String amount;
    private String statusPayment;

    public Tax(String billId, String bullettinCode, String year,
            String description, String expiringDate, String amount,
            String statusPayment) {
        this.billId = billId;
        this.bullettinCode = bullettinCode;
        this.year = year;
        this.description = description;
        this.expiringDate = expiringDate;
        this.amount = amount;
        this.statusPayment = statusPayment;
    }

    public Tax() {

    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBullettinCode() {
        return bullettinCode;
    }

    public void setBullettinCode(String bullettinCode) {
        this.bullettinCode = bullettinCode;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpiringDate() {
        return expiringDate;
    }

    public void setExpiringDate(String expiringDate) {
        this.expiringDate = expiringDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(String statusPayment) {
        this.statusPayment = statusPayment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((billId == null) ? 0 : billId.hashCode());
        result = prime * result
                + ((bullettinCode == null) ? 0 : bullettinCode.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((expiringDate == null) ? 0 : expiringDate.hashCode());
        result = prime * result + ((statusPayment == null) ? 0 : statusPayment.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tax other = (Tax) obj;
        if (amount == null) {
            if (other.amount != null) {
                return false;
            }
        } else if (!amount.equals(other.amount)) {
            return false;
        }
        if (billId == null) {
            if (other.billId != null) {
                return false;
            }
        } else if (!billId.equals(other.billId)) {
            return false;
        }
        if (bullettinCode == null) {
            if (other.bullettinCode != null) {
                return false;
            }
        } else if (!bullettinCode.equals(other.bullettinCode)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (expiringDate == null) {
            if (other.expiringDate != null) {
                return false;
            }
        } else if (!expiringDate.equals(other.expiringDate)) {
            return false;
        }
        if (statusPayment == null) {
            if (other.statusPayment != null) {
                return false;
            }
        } else if (!statusPayment.equals(other.statusPayment)) {
            return false;
        }
        if (year == null) {
            if (other.year != null) {
                return false;
            }
        } else if (!year.equals(other.year)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tax [billId=" + billId + ", bullettinCode=" + bullettinCode
                + ", year=" + year + ", description=" + description
                + ", expiringDate=" + expiringDate + ", amount=" + amount
                + ", statusPayment=" + statusPayment + "]";
    }

}
