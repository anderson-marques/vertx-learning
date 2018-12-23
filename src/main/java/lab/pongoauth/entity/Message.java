package lab.pongoauth.entity;

import java.util.Objects;

public class Message {

  private final String id;
  private String text = "";

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
  public void setText(String text) {
    this.text = text;
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
