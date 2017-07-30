/**
 * Trasport API
 * The Transport API allows interested developers to build their own applications using public timetable data, whether they're on the web, the desktop or mobile devices. The aim of this inofficial API is to cover public transport within Switzerland. If you are looking for an officially supported source or need to download all data e.g in GTFS format, please check opendata.swiss. The source code of the Transport API can be found on GitHub, please ask any technical questions there. If you need a direct contact write an email to transport@opendata.ch. In order to be kept update on the future development of this API, please subscribe to our low-traffic Google Group.
 *
 * OpenAPI spec version: 1
 * Contact: transport@opendata.ch
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.unstable.transport.model;

import ch.unstable.transport.model.Capacity1st;
import ch.unstable.transport.model.Capacity2nd;
import ch.unstable.transport.model.Checkpoint;
import ch.unstable.transport.model.Sections;
import ch.unstable.transport.model.Service;
import java.util.*;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * A connection represents a possible journey between two locations.
 **/
@ApiModel(description = "A connection represents a possible journey between two locations.")
public class Connection {
  
  @SerializedName("from")
  private Checkpoint from = null;
  @SerializedName("to")
  private Checkpoint to = null;
  @SerializedName("duration")
  private String duration = null;
  @SerializedName("service")
  private Service service = null;
  @SerializedName("products")
  private List<String> products = null;
  @SerializedName("capacity1st")
  private Capacity1st capacity1st = null;
  @SerializedName("capacity2nd")
  private Capacity2nd capacity2nd = null;
  @SerializedName("sections")
  private List<Sections> sections = null;

  /**
   * The departure checkpoint of the connection
   **/
  @ApiModelProperty(value = "The departure checkpoint of the connection")
  public Checkpoint getFrom() {
    return from;
  }
  public void setFrom(Checkpoint from) {
    this.from = from;
  }

  /**
   * The arrival checkpoint of the connection
   **/
  @ApiModelProperty(value = "The arrival checkpoint of the connection")
  public Checkpoint getTo() {
    return to;
  }
  public void setTo(Checkpoint to) {
    this.to = to;
  }

  /**
   * Duration of the journey
   **/
  @ApiModelProperty(value = "Duration of the journey")
  public String getDuration() {
    return duration;
  }
  public void setDuration(String duration) {
    this.duration = duration;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Service getService() {
    return service;
  }
  public void setService(Service service) {
    this.service = service;
  }

  /**
   * Array with transport products
   **/
  @ApiModelProperty(value = "Array with transport products")
  public List<String> getProducts() {
    return products;
  }
  public void setProducts(List<String> products) {
    this.products = products;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Capacity1st getCapacity1st() {
    return capacity1st;
  }
  public void setCapacity1st(Capacity1st capacity1st) {
    this.capacity1st = capacity1st;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Capacity2nd getCapacity2nd() {
    return capacity2nd;
  }
  public void setCapacity2nd(Capacity2nd capacity2nd) {
    this.capacity2nd = capacity2nd;
  }

  /**
   * A list of sections
   **/
  @ApiModelProperty(value = "A list of sections")
  public List<Sections> getSections() {
    return sections;
  }
  public void setSections(List<Sections> sections) {
    this.sections = sections;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Connection connection = (Connection) o;
    return (this.from == null ? connection.from == null : this.from.equals(connection.from)) &&
        (this.to == null ? connection.to == null : this.to.equals(connection.to)) &&
        (this.duration == null ? connection.duration == null : this.duration.equals(connection.duration)) &&
        (this.service == null ? connection.service == null : this.service.equals(connection.service)) &&
        (this.products == null ? connection.products == null : this.products.equals(connection.products)) &&
        (this.capacity1st == null ? connection.capacity1st == null : this.capacity1st.equals(connection.capacity1st)) &&
        (this.capacity2nd == null ? connection.capacity2nd == null : this.capacity2nd.equals(connection.capacity2nd)) &&
        (this.sections == null ? connection.sections == null : this.sections.equals(connection.sections));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.from == null ? 0: this.from.hashCode());
    result = 31 * result + (this.to == null ? 0: this.to.hashCode());
    result = 31 * result + (this.duration == null ? 0: this.duration.hashCode());
    result = 31 * result + (this.service == null ? 0: this.service.hashCode());
    result = 31 * result + (this.products == null ? 0: this.products.hashCode());
    result = 31 * result + (this.capacity1st == null ? 0: this.capacity1st.hashCode());
    result = 31 * result + (this.capacity2nd == null ? 0: this.capacity2nd.hashCode());
    result = 31 * result + (this.sections == null ? 0: this.sections.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Connection {\n");
    
    sb.append("  from: ").append(from).append("\n");
    sb.append("  to: ").append(to).append("\n");
    sb.append("  duration: ").append(duration).append("\n");
    sb.append("  service: ").append(service).append("\n");
    sb.append("  products: ").append(products).append("\n");
    sb.append("  capacity1st: ").append(capacity1st).append("\n");
    sb.append("  capacity2nd: ").append(capacity2nd).append("\n");
    sb.append("  sections: ").append(sections).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}