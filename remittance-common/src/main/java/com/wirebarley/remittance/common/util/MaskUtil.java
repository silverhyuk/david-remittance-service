package com.wirebarley.remittance.common.util;

public class MaskUtil {
    /**
     * 계좌번호 마스킹 처리
     * @param accountNumber 원본 계좌번호
     * @return 마스킹된 계좌번호
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return accountNumber;
        }

        int length = accountNumber.length();
        if (length <= 8) {
            // 계좌번호가 너무 짧은 경우 앞 2자리만 표시
            return accountNumber.substring(0, 2) + "*".repeat(length - 2);
        } else {
            // 앞 4자리, 뒤 4자리 표시, 중간 마스킹
            return accountNumber.substring(0, 4) + "*".repeat(length - 8) + accountNumber.substring(length - 4);
        }
    }
}
