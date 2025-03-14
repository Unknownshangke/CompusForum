package com.xzp.forum.model;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import lombok.Data;
import javax.persistence.*;

/**
 * answer的model实体层
 *
 * @author xiezhiping
 *
 */
@Data
@Entity
@Table(name = "answers")
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;//评论的answerId

	@Column(columnDefinition = "TEXT")
	private String content;//评论的内容

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "topic_id")
	private Topic topic;

	@Column(name = "created_date")
	private Date createdDate;//评论的创建时间

	@Column(name = "like_count")
	private int likeCount = 0;

	private boolean useful;//评论是否有用
	private String code;//评论附加的代码
	private Integer idTopic;//评论对应的话题的topicId
	private Integer idUser;//该话题的用户的userId

	public Integer getIdTopic() {
		return idTopic;
	}

	public void setIdTopic(Integer idTopic) {
		this.idTopic = idTopic;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isUseful() {
		return useful;
	}

	public void setUseful(boolean useful) {
		this.useful = useful;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String displayParsedCreatedDate() {
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatter.format(this.createdDate);
	}

	public String displayCode() {
		if (Optional.ofNullable(code).isPresent())
			return Optional.ofNullable(code).get();
		else
			return "";
	}

	public String displayBeginning() {
		return (this.content.length() < 32) ? this.content.concat("...") : this.content.substring(0, 30).concat("...");
	}
}
