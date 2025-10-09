package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReceiptItemDto;
import com.example.SanjiBE.dto.ReceiptRequest;
import com.example.SanjiBE.dto.ReceiptResponse;
import com.example.SanjiBE.dto.ReceiptSummaryResponse;
import com.example.SanjiBE.entity.*;
import com.example.SanjiBE.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReceiptResponse saveReceipt(String username, ReceiptRequest req){
        // 사용자 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
        // Shop 확인
        // 먼저 매장 있는 지 확인
        Shop shop = shopRepository.findByShopName(req.getStoreName())
                .orElseGet(() -> { // 업으면 매장 생성 후 리포지토리 저장
                    Shop shop1 = new Shop();
                    shop1.setShopName(req.getStoreName());
                    return shopRepository.save(shop1);
                });

        // 영수증 중복 로직
        LocalDate date = LocalDate.parse(req.getDate().replace(".", "-")); // LocalDate 형식으로
        int total = Integer.parseInt(req.getTotalAmount().replace(",", "")); // 비용 "," 제거
        boolean exists = receiptRepository.existsByUserAndShopAndReceiptDateAndTotalPrice(user, shop, date, total);
        if(exists){ // 영수증이 존재하면
            throw new RuntimeException("이미 저장된 영수증입니다.");
        }

        // 영수증 생성
        Receipt receipt = new Receipt();
        receipt.setUser(user);
        receipt.setShop(shop);
        receipt.setReceiptDate(date);
        receipt.setTotalPrice(total);
        receipt = receiptRepository.save(receipt);

        // 4. Purchase + Product 저장
        List<ReceiptItemDto> items = new ArrayList<>();
        for (ReceiptItemDto dto : req.getItemList()) { // 수정됨
            int price = Integer.parseInt(dto.getPrice().replace(",", ""));
            int qty = Integer.parseInt(dto.getQty().replace(",", ""));

            Product product = productRepository.findByShopAndProductName(shop, dto.getName())
                    .orElseGet(() -> {
                        Product p = new Product();
                        p.setShop(shop);
                        p.setProductName(dto.getName());
                        p.setProductPrice(price);
                        return productRepository.save(p);
                    });

            Purchase purchase = new Purchase();
            purchase.setReceipt(receipt);
            purchase.setProduct(product);
            purchase.setQuantity(qty);
            purchaseRepository.save(purchase);

            items.add(new ReceiptItemDto(
                    product.getId(),
                    dto.getName(),
                    dto.getPrice(),
                    dto.getQty(),
                    String.valueOf(price * qty)
            ));
        }

        return new ReceiptResponse(receipt.getId(), shop.getShopName(), req.getDate(), total, items);
    }

    @Transactional
    public List<ReceiptResponse> getReceiptsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

        List<Receipt> receipts = receiptRepository.findByUser(user);

        List<ReceiptResponse> responses = new ArrayList<>();
        for (Receipt r : receipts) {
            // Purchase → ReceiptItemDto 변환
            List<ReceiptItemDto> items = r.getPurchases().stream()
                    .map(p -> new ReceiptItemDto(
                            p.getProduct().getId(),
                            p.getProduct().getProductName(),
                            String.valueOf(p.getProduct().getProductPrice()),
                            String.valueOf(p.getQuantity()),
                            String.valueOf(p.getProduct().getProductPrice() * p.getQuantity())
                    ))
                    .toList();

            responses.add(new ReceiptResponse(
                    r.getId(),
                    r.getShop().getShopName(),
                    r.getReceiptDate().toString(),
                    r.getTotalPrice(),
                    items
            ));
        }
        return responses;
    }

    @Transactional
    @Override
    public ReceiptSummaryResponse getSummaryById(Long receiptId) {
        Receipt r = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptId));

        return ReceiptSummaryResponse.builder()
                .storeName(r.getShop().getShopName())
                .date(r.getReceiptDate().toString().replace("-", ".")) // yyyy.MM.dd 형태
                .totalAmount(r.getTotalPrice())
                .build();
    }
}
