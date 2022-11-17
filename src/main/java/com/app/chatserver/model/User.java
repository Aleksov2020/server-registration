package com.app.chatserver.model;

import java.util.Date;

import javax.persistence.*;


import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "avatar_id", referencedColumnName = "id")
	private Avatar avatar;
	@Column(name="date_birth")
	private Date dateBirth;
	@Column(name="user_phone")
	private String phone;
	@Column(name="user_name")
	private String userName;
	@Column(name="first_name")
	private String firstName;
	@Column(name="middle_name")
	private String middleName;
	@Column(name="last_name")
	private String lastName;
	@Column(name = "subscribers_counter")
	private Integer subscriberCounter;
	@Column(name = "subscribed_counter")
	private Integer subscribedCounter;
	@Column(name = "posts_counter")
	private Integer postsCounter;
	@Column(name="date_last_enter")
	private Date dateLastEnter;
	@Column(name="date_last_update")
	private Date dateLastUpdate;
	@Column(name="date_create")
	private Date dateCreate;
	@Column(name="is_active")
	private Boolean isActive;
	@Column(name="is_reported")
	private Boolean isReported;
	@Column(name="is_banned")
	private Boolean isBanned;
		
}
