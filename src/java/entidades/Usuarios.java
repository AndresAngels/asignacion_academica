package entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Aaron
 */
@Entity
@Table(name = "usuarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuarios.findAll", query = "SELECT u FROM Usuarios u"),
    @NamedQuery(name = "Usuarios.findByUId", query = "SELECT u FROM Usuarios u WHERE u.uId = :uId"),
    @NamedQuery(name = "Usuarios.findByULogin", query = "SELECT u FROM Usuarios u WHERE u.uLogin = :uLogin"),
    @NamedQuery(name = "Usuarios.findByUPassword", query = "SELECT u FROM Usuarios u WHERE u.uPassword = :uPassword"),
    @NamedQuery(name = "Usuarios.findByUNombre", query = "SELECT u FROM Usuarios u WHERE u.uNombre = :uNombre"),
    @NamedQuery(name = "Usuarios.findByUApellido", query = "SELECT u FROM Usuarios u WHERE u.uApellido = :uApellido"),
    @NamedQuery(name = "Usuarios.findByUEmail", query = "SELECT u FROM Usuarios u WHERE u.uEmail = :uEmail"),
    @NamedQuery(name = "Usuarios.findByUActivo", query = "SELECT u FROM Usuarios u WHERE u.uActivo = :uActivo"),
    @NamedQuery(name = "Usuarios.findByDivCodigo", query = "SELECT u FROM Usuarios u WHERE u.divCodigo = :divCodigo"),
    @NamedQuery(name = "Usuarios.findByDepa", query = "SELECT u FROM Usuarios u WHERE u.depa = :depa"),
    @NamedQuery(name = "Usuarios.findByCodigousuario", query = "SELECT u FROM Usuarios u WHERE u.codigousuario = :codigousuario"),
    @NamedQuery(name = "Usuarios.findByExtension", query = "SELECT u FROM Usuarios u WHERE u.extension = :extension")})
public class Usuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "u_id")
    private int uId;
    @Id
    @Basic(optional = false)
    @Column(name = "u_login")
    private String uLogin;
    @Basic(optional = false)
    @Column(name = "u_password")
    private String uPassword;
    @Basic(optional = false)
    @Column(name = "u_nombre")
    private String uNombre;
    @Column(name = "u_apellido")
    private String uApellido;
    @Basic(optional = false)
    @Column(name = "u_email")
    private String uEmail;
    @Basic(optional = false)
    @Column(name = "u_activo")
    private short uActivo;
    @Column(name = "div_codigo")
    private Short divCodigo;
    @Column(name = "depa")
    private Integer depa;
    @Basic(optional = false)
    @Column(name = "codigousuario")
    private int codigousuario;
    @Column(name = "extension")
    private String extension;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uLogin")
    private List<Horario> horarioList;
    @JoinColumn(name = "codigo_perfil", referencedColumnName = "codigo_perfil")
    @ManyToOne(optional = false)
    private Perfiles codigoPerfil;

    public Usuarios() {
    }

    public Usuarios(String uLogin) {
        this.uLogin = uLogin;
    }

    public Usuarios(String uLogin, int uId, String uPassword, String uNombre, String uEmail, short uActivo, int codigousuario) {
        this.uLogin = uLogin;
        this.uId = uId;
        this.uPassword = uPassword;
        this.uNombre = uNombre;
        this.uEmail = uEmail;
        this.uActivo = uActivo;
        this.codigousuario = codigousuario;
    }

    public int getUId() {
        return uId;
    }

    public void setUId(int uId) {
        this.uId = uId;
    }

    public String getULogin() {
        return uLogin;
    }

    public void setULogin(String uLogin) {
        this.uLogin = uLogin;
    }

    public String getUPassword() {
        return uPassword;
    }

    public void setUPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public String getUNombre() {
        return uNombre;
    }

    public void setUNombre(String uNombre) {
        this.uNombre = uNombre;
    }

    public String getUApellido() {
        return uApellido;
    }

    public void setUApellido(String uApellido) {
        this.uApellido = uApellido;
    }

    public String getUEmail() {
        return uEmail;
    }

    public void setUEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public short getUActivo() {
        return uActivo;
    }

    public void setUActivo(short uActivo) {
        this.uActivo = uActivo;
    }

    public Short getDivCodigo() {
        return divCodigo;
    }

    public void setDivCodigo(Short divCodigo) {
        this.divCodigo = divCodigo;
    }

    public Integer getDepa() {
        return depa;
    }

    public void setDepa(Integer depa) {
        this.depa = depa;
    }

    public int getCodigousuario() {
        return codigousuario;
    }

    public void setCodigousuario(int codigousuario) {
        this.codigousuario = codigousuario;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @XmlTransient
    public List<Horario> getHorarioList() {
        return horarioList;
    }

    public void setHorarioList(List<Horario> horarioList) {
        this.horarioList = horarioList;
    }

    public Perfiles getCodigoPerfil() {
        return codigoPerfil;
    }

    public void setCodigoPerfil(Perfiles codigoPerfil) {
        this.codigoPerfil = codigoPerfil;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uLogin != null ? uLogin.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuarios)) {
            return false;
        }
        Usuarios other = (Usuarios) object;
        if ((this.uLogin == null && other.uLogin != null) || (this.uLogin != null && !this.uLogin.equals(other.uLogin))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Usuarios[ uLogin=" + uLogin + " ]";
    }

}
