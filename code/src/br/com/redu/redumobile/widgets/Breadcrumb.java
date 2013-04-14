package br.com.redu.redumobile.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.developer.redu.models.Status;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class Breadcrumb extends TextView {

	public Breadcrumb(Context context) {
		super(context);
		init();
	}
	
	public Breadcrumb(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Breadcrumb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setMaxLines(2);
		setEllipsize(TruncateAt.START);
	}
	
	public void setStatus(Status status) {
		List<String> crumbs = getCrumbs(status);
		
		if(crumbs == null) {
			setVisibility(View.GONE);
		} else {
			String breadcrumbs = makeBreadcrumbs(crumbs);
			setText(Html.fromHtml(breadcrumbs));
		}
	}
	
	static private List<String> getCrumbs(Status status) {
		String environment = status.getEnvironmentName();
		String course = status.getCourseName();
		String space = status.getSpaceName();
		String subject = status.getSubjectName();
		String lecture = status.getLectureName();
		
		List<String> crumbs = null;
		if(environment != null) {
			crumbs = new ArrayList<String>();
			crumbs.add(environment);

			if(course != null) {
				crumbs.add(course);
			
				if(space != null) {
					crumbs.add(space);
				
					if(subject != null) {
						crumbs.add(subject);
					
						if(lecture != null) {
							crumbs.add(lecture);
						}
					}
				}
			}
		}
		
		return crumbs;
	}
	
	static private String makeBreadcrumbs(List<String> crumbs) {
		StringBuffer sb = new StringBuffer();
		
		Iterator<String> it = crumbs.iterator();
		while(it.hasNext()) {
			String s = it.next();
			if(it.hasNext()) {
				sb.append(s).append(" > ");
			} else {
				sb.append("<b>").append(s).append("</b>");
			}
		}
		
		return sb.toString();
	}
}
