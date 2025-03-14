package com.xzp.forum.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xzp.forum.util.DateUtils;

/**
 * User的model实体层
 *
 * @author xiezhiping
 *
 */
@Entity
@Table(name = "user")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	private String avatar;

	@Column(name = "is_admin")
	private boolean isAdmin = false;

	@Column(name = "is_moderator")
	private boolean isModerator = false;

	@Column(name = "is_banned")
	private boolean isBanned = false;

	@Column(name = "ban_reason")
	private String banReason;

	@Column(name = "unban_date")
	private Date unbanDate;

	@ElementCollection
	@CollectionTable(name = "user_moderator_sections", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "section")
	private Set<String> moderatorSections = new HashSet<>();

	@Column(columnDefinition = "TEXT")
	private String introduction;

	@Column(name = "created_date")
	private Date createdDate;

	private String role = "USER";

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Answer> answers = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Topic> topics = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		this.isAdmin = admin;
		if (admin) {
			this.role = "ADMIN";
			this.isModerator = false;
		} else if (!this.isModerator) {
			this.role = "USER";
		}
	}

	public boolean isModerator() {
		return isModerator;
	}

	public void setModerator(boolean moderator) {
		this.isModerator = moderator;
		if (moderator) {
			this.role = "MODERATOR";
			this.isAdmin = false;
		} else if (!this.isAdmin) {
			this.role = "USER";
		}
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public String getBanReason() {
		return banReason;
	}

	public void setBanReason(String banReason) {
		this.banReason = banReason;
	}

	public Date getUnbanDate() {
		return unbanDate;
	}

	public void setUnbanDate(Date unbanDate) {
		this.unbanDate = unbanDate;
	}

	public Set<String> getModeratorSections() {
		return moderatorSections;
	}

	public void setModeratorSections(Set<String> moderatorSections) {
		this.moderatorSections = moderatorSections;
	}

	public void addModeratorSection(String section) {
		this.moderatorSections.add(section);
	}

	public void removeModeratorSection(String section) {
		this.moderatorSections.remove(section);
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
		// 同步角色标志位
		switch (role) {
			case "ADMIN":
				this.isAdmin = true;
				this.isModerator = false;
				break;
			case "MODERATOR":
				this.isAdmin = false;
				this.isModerator = true;
				break;
			case "USER":
				this.isAdmin = false;
				this.isModerator = false;
				break;
		}
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();

		// 添加基本角色
		authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

		// 添加管理员权限
		if (isAdmin) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		// 添加版主权限
		if (isModerator) {
			authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
			// 添加版块特定权限
			for (String section : moderatorSections) {
				authorities.add(new SimpleGrantedAuthority("SECTION_" + section.toUpperCase()));
			}
		}

		return authorities;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !isBanned;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return !isBanned;
	}

	public String displayParsedDate() {
		return DateUtils.getParseDate(this.createdDate);
	}

	public String displayContentOfOptional() {
		return introduction != null ? introduction : "";
	}

	public boolean hasModeratorPermission(String section) {
		return isAdmin || (isModerator && moderatorSections.contains(section));
	}
}
