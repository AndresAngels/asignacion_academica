/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AndresAngel
 */
@Entity
@Table(name = "plan")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plan.findAll", query = "SELECT p FROM Plan p"),
    @NamedQuery(name = "Plan.findByCodplan", query = "SELECT p FROM Plan p WHERE p.codplan = :codplan"),
    @NamedQuery(name = "Plan.findByDescripcion", query = "SELECT p FROM Plan p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Plan.findByEstado", query = "SELECT p FROM Plan p WHERE p.estado = :estado")})
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codplan")
    private String codplan;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;

    public Plan() {
    }

    public Plan(String codplan) {
        this.codplan = codplan;
    }

    public Plan(String codplan, String descripcion, int estado) {
        this.codplan = codplan;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getCodplan() {
        return codplan;
    }

    public void setCodplan(String codplan) {
        this.codplan = codplan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codplan != null ? codplan.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Plan)) {
            return false;
        }
        Plan other = (Plan) object;
        if ((this.codplan == null && other.codplan != null) || (this.codplan != null && !this.codplan.equals(other.codplan))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Plan[ codplan=" + codplan + " ]";
    }

}
