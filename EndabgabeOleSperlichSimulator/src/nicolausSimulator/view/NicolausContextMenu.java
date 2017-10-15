package nicolausSimulator.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import nicolausSimulator.model.Invisible;
import nicolausSimulator.model.Nicolaus;
import nicolausSimulator.model.Territory;

public class NicolausContextMenu extends ContextMenu {

	Nicolaus nick;

	/**
	 * a ContextMenu to show when right-clicked on Nicolaus. the menu items are the
	 * methods of the Nicolaus
	 * 
	 * @param territory
	 * @param nick
	 */
	public NicolausContextMenu(Territory territory, Nicolaus nick) {
		this.nick = nick;
		ArrayList<Method> methods = this.getMethods(territory.getNick());
		for (Method method : methods) {
			MenuItem item = new MenuItem(generateMethodName(method));
			if (method.getParameterTypes().length == 0) {
				item.setOnAction(e -> {
					try {
						method.setAccessible(true);
						method.invoke(territory.getNick());
					} catch (IllegalAccessException e1) {
					} catch (IllegalArgumentException e1) {
					} catch (InvocationTargetException e1) {
						new DeathSound().play();
					}
				});

			} else {
				item.setDisable(true);
			}
			this.getItems().add(item);
		}
	}

	/**
	 * add return type and parameters to method name
	 * 
	 * @param method
	 * @return
	 */
	private static String generateMethodName(Method method) {
		StringBuilder methodName = new StringBuilder();
		methodName.append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
		Class<?>[] params = method.getParameterTypes();
		for (int count = 0; count < params.length; count++) {
			methodName.append(params[count].getName());
			if (count != params.length - 1) {
				methodName.append(", ");
			}
		}
		methodName.append(")");
		return methodName.toString();
	}

	/**
	 * list the public, non-static, non-static methods without Invisible annotation
	 * from model.Nicolaus and model.Nicolaus.<users' class>
	 * 
	 * @param nick
	 * @return
	 */
	private ArrayList<Method> getMethods(Nicolaus nick) {

		Method[] nativeMethods = Nicolaus.class.getDeclaredMethods();
		Method[] newMethods = this.nick.getClass().getDeclaredMethods();

		ArrayList<Method> allMethods = new ArrayList<Method>();
		
		for (Method method : newMethods) {
			if(Modifier.isPublic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())
					&& !Modifier.isStatic(method.getModifiers()) && !method.isAnnotationPresent(Invisible.class) ) {
				allMethods.add(method);
			}
		}
		for (Method method : nativeMethods) {
			if(!allMethods.contains(method) && Modifier.isPublic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())
					&& !Modifier.isStatic(method.getModifiers()) && !method.isAnnotationPresent(Invisible.class)) {
				allMethods.add(method);
			}
		}
		return allMethods;
	}
}
