package com.xzp.forum.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import javax.persistence.*;

/**
 * topic的model实体层
 * @author xiezhiping
 *
 */
@Data
@Entity
@Table(name = "topics")
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //话题的topicId

	@Column(nullable = false)
	private String title;//话题的标题

	@Column(columnDefinition = "TEXT")
	private String content;//话题的内容

	@Column(nullable = false)
	private String section;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "created_date")
	private Date createdDate; //话题的创建时间

	@Column(name = "is_locked")
	private boolean isLocked = false;

	@Column(name = "view_count")
	private int viewCount = 0;

	@Column(name = "like_count")
	private int likeCount = 0;

	@Column(name = "comment_count")
	private int commentCount = 0;

	private String category;//话题所属的目录
	private String code;//话题附加的代码

	private Integer idUser;//创建话题的用户的userId

	@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
	private List<Answer> answers;

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
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
}
