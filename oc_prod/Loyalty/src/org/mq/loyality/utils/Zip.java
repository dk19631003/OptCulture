package org.mq.loyality.utils;


/**
 * Very simple model of a City. It has a reference to its State to be almost
 * unique. (There are two Springfields in NJ for example; one in Burlington and
 * another in Union)
 * 
 * The id is unused but is there to use in JPA or Hibernate.
 * 
 * @author gene
 * 
 */
public class Zip implements Comparable<Zip> {
	private Long id;
	private String zip;
	private City city;

	public Zip() {
		this.zip = "unknown";
		this.setCity(new City("unknown"));
		this.id = System.currentTimeMillis();
	}

	public Zip(String zip) {
		this.zip = zip;
		this.setCity(new City("unknown"));
		this.id = System.currentTimeMillis();
	}

	public int compareTo(Zip o) {
		return this.zip.compareTo(o.zip);
	}

	

	public Long getId() {
		return this.id;
	}

	public String getzip() {
		return this.zip;
	}

	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Zip other = (Zip) obj;
		if (getCity() == null) {
			if (other.getCity() != null)
				return false;
		} else if (!getCity().equals(other.getCity()))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setzip(String zip) {
		this.zip = zip;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	

	



	
}
