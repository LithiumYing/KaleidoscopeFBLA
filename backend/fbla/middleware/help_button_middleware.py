from textwrap import dedent
from django.shortcuts import reverse


class HelpButtonMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        response = self.get_response(request)

        if request.path.startswith(reverse("admin:index")):
            response.content += dedent(
                """
            <script>
                function openHelpPopup() {
                    window.open("https://kaleidoscope-fbla.herokuapp.com/help", "", "width=996, height=833");
                }
            </script>
            <style>
                .help-button {
                    position: fixed;
                    bottom: 20px;
                    right: 20px;
                    width: 50px;
                    height: 50px;
                    border-radius: 50%;
                    background-color: #112E51;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    cursor: pointer;
                    color: white;
                    font-weight: bold;
                    font-size: 20px;
                    box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.5);
                    z-index: 9999;
                    line-height: 1;
                }
            </style>
            <div class="help-button" onclick="openHelpPopup()">?</div>
            """
            ).encode("utf-8")

        return response
