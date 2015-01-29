function toggleExpand( boxID, buttonID )
{
	var box = document.getElementById( boxID );
	var button = document.getElementById( buttonID );
	var collapsed = "[+]";
	var expanded = "[-]";
	
	if ( button.innerHTML == collapsed )
	{
		box.style.maxHeight = "none";
		button.innerHTML = expanded;
	}
	else
	{
		box.style.maxHeight = "100px";
		button.innerHTML = collapsed;
	}
}

jQuery(document).ready(function($) {
    $(".clickableRow").click(function() {
          window.document.location = $(this).attr("href");
    });
    $("body").tooltip({ selector: '[data-toggle=tooltip]' });
});
