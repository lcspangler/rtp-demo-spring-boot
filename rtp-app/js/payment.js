
class Payment {
    
    constructor (config) {
        this.config = config;
    }

    getTransaction() {
        return new Promise((resolve, reject) => {
            $.ajax(this.config.transaction).success(function(res) {
                resolve(res);
            }).error(function(err) {
                reject(err);
            });
        });
    }

    sendPayment(params) {
        return new Promise((resolve, reject) => {
            var settings = {
                "crossDomain": true,
                "url": this.config.payment,
                "method": "POST",
                "data": JSON.stringify(params)
            }
            $.ajax(settings).done(function (response) {
                resolve(response);
            });
            
        });
    }

    setAccountId(id) {
        this.accountId = id;
    }

    getAccountId(id) {
        return this.accountId;
    }

    getProfile() {
        return {
            id: this.accountId,
            firstName: 'Liz',
            lastName: 'Spangler',
            balance: '200',
            email: 'liz@gmail.com'
        }
    }
}
