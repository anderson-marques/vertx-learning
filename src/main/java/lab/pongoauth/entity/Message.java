package lab.pongoauth.entity;

import java.io.Serializable;
import java.util.Objects;

import io.vertx.core.json.JsonObject;

public class Message implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String id;
  private String text = "";

  public static Message createFromJsonString(String messageJson) {
    try {
      JsonObject jsonObject = new JsonObject(messageJson);
      return createFromJson(jsonObject);
    } catch (Exception e) {
      throw new IllegalArgumentException();
    }
  }

  public static Message createFromJson(JsonObject messageJson) {
    try {
      return new Message(messageJson.getString("id"))
                .setText(messageJson.getString("text"));
    } catch (Exception e) {
      throw new IllegalArgumentException();
    }
  }

  public Message(final String id) {
    this.id = id;
  }

  /**
   * Returns the id of the message.
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the text value of the message.
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text value of the message.
   * 
   * @param text the text to set
   */
  public Message setText(String text) {
    this.text = text;
    return this;
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("id", this.id);
    jsonObject.put("text", this.text);
    return jsonObject;
  }

  public String toJsonString() {
    return this.toJson().toString();
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    return other instanceof Message && this.id.equals(((Message) other).getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
