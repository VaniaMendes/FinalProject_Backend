
    package aor.paj.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

    @XmlRootElement
    public class LoginDto {

        String username;
        String password;

        public LoginDto(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public LoginDto(){}

        @XmlElement
        public String getUsername() {
            return username;
        }

        public void setUsername(String email) {
            this.username = email;
        }
        @XmlElement
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

