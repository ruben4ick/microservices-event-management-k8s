package ua.edu.ukma.user_service.user;//package ua.edu.ukma.user_service.user.internal;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class UserPrincipal implements UserDetails {
//
//	private final transient User user;
//
//	public UserPrincipal(User user) {
//		this.user = user;
//	}
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		UserRole userRole = user.getUserRole();
//
//		List<GrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority(userRole.name()));
//
//        if (userRole == UserRole.ADMIN) {
//            authorities.add(new SimpleGrantedAuthority(UserRole.ORGANIZER.name()));
//            authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
//        } else if (userRole == UserRole.ORGANIZER) {
//            authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
//        }
//
//		return authorities;
//	}
//
//	@Override
//	public String getPassword() {
//		return user.getPassword();
//	}
//
//	@Override
//	public String getUsername() {
//		return user.getUsername();
//	}
//}
