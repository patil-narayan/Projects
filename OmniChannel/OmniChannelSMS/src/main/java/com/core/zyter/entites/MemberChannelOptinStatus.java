package com.core.zyter.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBER_CHANNEL_OPTIN_MAPPING")
public class MemberChannelOptinStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_OPTIN_ID")
    String userOptinId;
    @Column(name = "MEMBER")
    String member;
    @Column(name = "CHANNEL_TYPE")
    String channelType;
    @Column(name = "OPTIN_STATUS")
    String optinStatus;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON" ,nullable = false)
    Date createdOn;
    @PrePersist
    void onCreate(){
        createdOn=new Date();
    }

}
