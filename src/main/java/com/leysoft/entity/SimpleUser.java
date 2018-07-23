
package com.leysoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(
        name = "users")
public class SimpleUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty
    private String username;

    @Column
    @NotEmpty
    private String password;

    @ManyToMany(
            cascade = {
                CascadeType.ALL
            },
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_user",
            joinColumns = {
                @JoinColumn(
                        name = "user_id",
                        nullable = false)
            },
            inverseJoinColumns = {
                @JoinColumn(
                        name = "rol_id",
                        nullable = false)
            })
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
