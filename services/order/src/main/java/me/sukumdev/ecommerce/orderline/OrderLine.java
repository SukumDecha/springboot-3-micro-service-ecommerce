package me.sukumdev.ecommerce.orderline;

import jakarta.persistence.*;
import lombok.*;
import me.sukumdev.ecommerce.order.Order;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class OrderLine {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    // default is var name + _id
    private Order order;
    private Integer productId;
    private double quantity;
}
