/**
 * Created by V on 29.11.2015.
 */
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Statistics {
    private String group;
    private Integer count;
    private Long age;

    public String getGroup() {
        return group;
    }

    public Integer getCount() {
        return count;
    }

    public Long getAge() {
        return age;
    }
    public void ChangeGroup(String group_){
        group = group_;
    }
    public void ChangeCount( Integer count_){
        count = count_;
    }
    public void ChangeAge (Long age_){
        age = age_;
    }

    public Statistics(String group, Integer count, Long age) {
        this.group = group;
        this.count = count;
        this.age = age;
    }
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(getClass() != o.getClass()){
            return false;
        }
        Statistics st = (Statistics) o;
        return (o == this);
    }
    public int hashCode(){
        final int prime = 31;
        return new HashCodeBuilder(getCount()%2==0?getCount()+1:getCount(), prime).toHashCode();
    }

    @Override
    public String toString() {
        return "Statistics{"
                + "group='" + group + '\''
                + ", count=" + count
                + ", age=" + age
                + '}';
    }
}
