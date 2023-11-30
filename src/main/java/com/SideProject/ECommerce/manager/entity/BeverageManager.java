package com.SideProject.ECommerce.manager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "BEVERAGE_MANAGER", schema = "LOCAL")
public class BeverageManager {

	@Id
	@Column(name = "IDENTIFICATION_NO")
	private String identificationNo;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "CUSTOMER_NAME")
	private String customerName;

}
