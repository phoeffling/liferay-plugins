/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.testpacl.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.service.PortalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class TestPACLUtil {

	public static Map<String, Boolean> testCurrentThread(long userId) {
		Map<String, Boolean> results = new HashMap<String, Boolean>();

		try {
			PortalServiceUtil.getBuildNumber();

			results.put("PortalServiceUtil#getBuildNumber", true);
		}
		catch (SecurityException se) {
			results.put("PortalServiceUtil#getBuildNumber", false);
		}
		catch (Exception e) {
			results.put("PortalServiceUtil#getBuildNumber", false);
		}

		try {
			UserLocalServiceUtil.getUser(userId);

			results.put("UserLocalServiceUtil#getUser", false);
		}
		catch (SecurityException se) {
			results.put("UserLocalServiceUtil#getUser", true);
		}
		catch (Exception e) {
			results.put("UserLocalServiceUtil#getUser", false);
		}

		return results;
	}

	public static Map<String, Boolean> testMessageBusThread(long userId)
		throws Exception {

		Message message = new Message();

		message.put("userId", userId);

		return (Map<String, Boolean>)MessageBusUtil.sendSynchronousMessage(
			"liferay/test_pacl", message);
	}

	public static Map<String, Boolean> testNewThread(final long userId)
		throws Exception {

		final Map<String, Boolean> results = new HashMap<String, Boolean>();

		Thread thread = new Thread () {

			@Override
			public void run() {
				results.putAll(testCurrentThread(userId));
			}

		};

		thread.start();

		try {
			thread.join();
		}
		catch (InterruptedException ie) {
			_log.error(ie, ie);
		}

		return results;
	}

	private static Log _log = LogFactoryUtil.getLog(TestPACLUtil.class);

}