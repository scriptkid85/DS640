package cmu.ds.mr.mapred;


import java.io.Serializable;

/**
 * JobID is used to privide the identifier for job
 * 
 * @author Guanyu Wang 
 * */

@SuppressWarnings("serial")
public class JobID implements Comparable<JobID>, Serializable {
	private String jobStartDate;
	private int id;

	public JobID(String jobStartDate, int id) {
		this.id = id;
		this.jobStartDate = jobStartDate;
	}

	public JobID() {
		jobStartDate = "";
	}

	public String getJobStartDate() {
		return jobStartDate;
	}

	@Override
	public int compareTo(JobID o) {
		JobID that = (JobID) o;
		int jtComp = this.jobStartDate.compareTo(that.jobStartDate);
		if (jtComp == 0) {
			return this.id - that.id;
		} else
			return jtComp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((jobStartDate == null) ? 0 : jobStartDate.hashCode());
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
		JobID other = (JobID) obj;
		if (id != other.id)
			return false;
		if (jobStartDate == null) {
			if (other.jobStartDate != null)
				return false;
		} else if (!jobStartDate.equals(other.jobStartDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("job_%s_%04d", jobStartDate, id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
