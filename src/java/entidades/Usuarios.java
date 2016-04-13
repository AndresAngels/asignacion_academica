package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AndresAngel
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
    @NamedQuery(name = "Usuarios.findByCodigoPerfil", query = "SELECT u FROM Usuarios u WHERE u.codigoPerfil = :codigoPerfil")})
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
    @Basic(optional = false)
    @Column(name = "codigo_perfil")
    private String codigoPerfil;
    @Transient
    private String nombreLogin;
    @Transient
    private String login;
    @Transient
    private String nombre;
    @Transient
    private String apellido;

    public Usuarios() {
    }

    public Usuarios(String uLogin) {
        this.uLogin = uLogin;
    }

    public Usuarios(String uLogin, int uId, String uPassword, String uNombre, String uEmail, short uActivo, String codigoPerfil) {
        this.uLogin = uLogin;
        this.uId = uId;
        this.uPassword = uPassword;
        this.uNombre = uNombre;
        this.uEmail = uEmail;
        this.uActivo = uActivo;
        this.codigoPerfil = codigoPerfil;
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


    public String getCodigoPerfil() {
        return codigoPerfil;
    }

    public void setCodigoPerfil(String codigoPerfil) {
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

    /**
     * @return the nombreLogin
     */
    public String getNombreLogin() {
        nombreLogin = uNombre + " " + uApellido;
        return nombreLogin;
    }

    /**
     * @param nombreLogin the nombreLogin to set
     */
    public void setNombreLogin(String nombreLogin) {
        this.nombreLogin = nombreLogin;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        login = uLogin;
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        nombre = uNombre;
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the apellido
     */
    public String getApellido() {
        apellido = uApellido;
        return apellido;
    }

    /**
     * @param apellido the apellido to set
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
