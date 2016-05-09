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
 * @author Aaron
 */
@Entity
@Table(name = "perfiles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Perfiles.findAll", query = "SELECT p FROM Perfiles p"),
    @NamedQuery(name = "Perfiles.findByCodigoPerfil", query = "SELECT p FROM Perfiles p WHERE p.codigoPerfil = :codigoPerfil"),
    @NamedQuery(name = "Perfiles.findByDescripcionPerfil", query = "SELECT p FROM Perfiles p WHERE p.descripcionPerfil = :descripcionPerfil")})
public class Perfiles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo_perfil")
    private String codigoPerfil;
    @Column(name = "descripcion_perfil")
    private String descripcionPerfil;

    public Perfiles() {
        //Contructor vacio para limpriar campos
    }

    public Perfiles(String codigoPerfil) {
        this.codigoPerfil = codigoPerfil;
    }

    public String getCodigoPerfil() {
        return codigoPerfil;
    }

    public void setCodigoPerfil(String codigoPerfil) {
        this.codigoPerfil = codigoPerfil;
    }

    public String getDescripcionPerfil() {
        return descripcionPerfil;
    }

    public void setDescripcionPerfil(String descripcionPerfil) {
        this.descripcionPerfil = descripcionPerfil;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigoPerfil != null ? codigoPerfil.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Perfiles)) {
            return false;
        }
        Perfiles other = (Perfiles) object;
        if ((this.codigoPerfil == null && other.codigoPerfil != null) || (this.codigoPerfil != null && !this.codigoPerfil.equals(other.codigoPerfil))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Perfiles[ codigoPerfil=" + codigoPerfil + " ]";
    }

}
