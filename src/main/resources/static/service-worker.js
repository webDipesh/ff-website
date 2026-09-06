self.addEventListener("push", function (event) {

    if (!event.data) {
        return;
    }

    let data = {};

    try {
        data = event.data.json();
    } catch (error) {
        data = {
            title: "FF Website",
            body: event.data.text()
        };
    }

    const title = data.title || "FF Website";

    const options = {
        body: data.body || "You have a new notification.",
        icon: "/images/icon-192.png",
        badge: "/images/icon-192.png",
        vibrate: [200, 100, 200],
        silent: false,
        requireInteraction: false,
        data: {
            url: data.url || "/notifications"
        }
    };

    event.waitUntil(
        self.registration.showNotification(
            title,
            options
        )
    );
});


self.addEventListener(
    "notificationclick",
    function (event) {

        event.notification.close();

        const targetUrl =
            event.notification.data &&
                event.notification.data.url
                ? event.notification.data.url
                : "/notifications";

        const absoluteUrl =
            new URL(
                targetUrl,
                self.location.origin
            ).href;

        event.waitUntil(

            clients.matchAll({
                type: "window",
                includeUncontrolled: true
            })

                .then(function (clientList) {

                    for (const client of clientList) {

                        if ("navigate" in client) {

                            return client
                                .navigate(absoluteUrl)
                                .then(function () {

                                    return client.focus();
                                });
                        }
                    }

                    if (clients.openWindow) {

                        return clients.openWindow(
                            absoluteUrl
                        );
                    }
                })
        );
    }
);