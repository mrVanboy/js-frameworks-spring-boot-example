package com.etnetera.hr.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 * 
 * @author Etnetera
 *
 */
@Entity
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 30)
	@NotNull
	private String name;

	@Column(nullable = false, length = 10)
	@NotNull
	private String version;



	@Column(nullable = false)
	@Positive
	private Long deprecationDate;

	@Column(nullable = false)
	@Positive
	private byte hypeLevel;
	
	public JavaScriptFramework() {
	}

	public JavaScriptFramework(
			String name,
			String version,
			Long deprecationDate,
			byte hypeLevel
	) {
		this.name = name;
		this.version = version;
		this.deprecationDate = deprecationDate;
		this.hypeLevel = hypeLevel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}

	public Long getDeprecationDate() {
		return deprecationDate;
	}

	public void setDeprecationDate(Long deprecationDate) {
		this.deprecationDate = deprecationDate;
	}

	public byte getHypeLevel() {
		return hypeLevel;
	}

	public void setHypeLevel(byte hypeLevel) {
		this.hypeLevel = hypeLevel;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
