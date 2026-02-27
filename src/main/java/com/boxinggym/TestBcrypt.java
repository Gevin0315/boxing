package com.boxinggym;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 临时测试类 - 用于生成 BCrypt 哈希
 */
public class TestBcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 生成新的哈希
        String hashed = encoder.encode(password);
        System.out.println("新哈希: " + hashed);
        
        // 验证数据库中的哈希值
        String dbHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
        System.out.println("数据库哈希: " + dbHash);
        System.out.println("数据库哈希长度: " + dbHash.length());
        System.out.println("验证数据库哈希: " + encoder.matches(password, dbHash));
        
        // BCrypt 哈希应该是60个字符
        System.out.println("新哈希长度: " + hashed.length());
    }
}
