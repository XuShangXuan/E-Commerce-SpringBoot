package com.SideProject.ECommerce.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"identificationNo"})
@ToString(exclude = {"beverageOrders"})
@Entity
@Table(name = "BEVERAGE_MEMBER", schema="LOCAL")
public class BeverageMember {

	@Id
	@Column(name = "IDENTIFICATION_NO")
	private String identificationNo; 
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@JsonIgnore
	@OneToMany(
		mappedBy = "beverageMember",
		cascade = {CascadeType.ALL}, 
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	@OrderBy(value = "orderID")
	private List<BeverageOrder> beverageOrders;
	
}
