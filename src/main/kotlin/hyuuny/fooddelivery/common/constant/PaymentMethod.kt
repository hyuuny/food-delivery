package hyuuny.fooddelivery.common.constant

enum class PaymentMethod(val value: String) {
    CREDIT_CARD("신용카드"),
    DEBIT_CARD("직불/체크카드"),
    MOBILE_PAYMENT("모바일결제"),
    BANK_TRANSFER("계좌이체"),
    CASH_ON_DELIVERY("현금결제"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
}
