
#
#  EN: SEND SMS
#  EN: Let's send a SMS, set well the YOUR_TOKEN and the YOUR_INTERNATIONAL_PHONE_NUMBER.
#
#  PT: ENVIAR SMS
#  PT: Vamos enviar um SMS, configure o YOUR_TOKEN e o YOUR_INTERNATIONAL_PHONE_NUMBER.
#

sendSMS = _remote.init()

sendSMS.setAuthorization("Bearer YOUR_TOKEN")

sms = _val.init()
    .set("From", "Netuno.org")
    .set("To", "YOUR_INTERNATIONAL_PHONE_NUMBER")
    .set("Text", "Hello from Netuno!")

_out.json(
    sendSMS.post("https://api.mailjet.com/v4/sms-send", sms)
)
