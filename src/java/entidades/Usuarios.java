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
import javax.persistence.Transient;
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
    @NamedQuery(name = "Usuarios.findByUActivo", query = "SELECT u FROM Usuarios u WHERE u.uActivo = :uActivo")})
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uLogin")
    private List<Horario> horarioList;
    @JoinColumn(name = "codigo_perfil", referencedColumnName = "codigo_perfil")
    @ManyToOne(optional = false)
    private Perfiles codigoPerfil;
    @JoinColumn(name = "id_plan", referencedColumnName = "id_plan")
    @ManyToOne
    private Plan idPlan;
    @Transient
    private String nombreLogin;
    @Transient
    private String login;
    @Transient
    private String nombre;
    @Transient
    private String apellido;

    public Usuarios() {
        //Contructor vacio para limpriar campos
    }

    public Usuarios(String uLogin) {
        this.uLogin = uLogin;
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

    public Plan getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Plan idPlan) {
        this.idPlan = idPlan;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uLogin != null ? uLogin.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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
