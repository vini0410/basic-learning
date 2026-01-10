package org.acme.repository.entity;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;


//@MongoEntity(collection = "users")
public class UserEntity
//        extends PanacheMongoEntity
{

    @BsonId
    public ObjectId id;
    public String name;
    public String surname;
    public String email;
    public String nickname;
    public String password;
    public List<AddressEntity> addresses;

}
