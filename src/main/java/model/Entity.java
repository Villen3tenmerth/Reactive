package model;

import org.bson.Document;

public interface Entity {
    Document toDocument();
}
