<?php
    function get($name, $default) {
        $value = $_GET[$name];
        if (empty($value)) {
            $value = $default;
        }
        return $value;
    }

    $comic_number = (int)get("comic", 1);

    $comics = glob("comics/*.*");
    $comic = $comics[$comic_number - 1];
    $numbering = $comic_number . " / " . count($comics);
    $previous = $comic_number > 1 ? $comic_number - 1 : $comic_number;
    $next = $comic_number < count($comics) ? $comic_number + 1 : $comic_number;
?>

<html>
    <head>
        <style>
            a {
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <div>
            <a href="comics.php?comic=<?=$previous?>">&lt;</a>
            <?=$numbering?>
            <a href="comics.php?comic=<?=$next?>">&gt;</a>
        </div>
        <div>
            <a href="comics.php?comic=<?=$next?>">
                <img src="<?=$comic?>" border="0">
            </a>
        </div>
        <div>
            <table>
<?php for ($i = 0; $i < count($comics); $i++) { ?>
                <tr>
                    <td>
                        <?=$i + 1?>
                        <a href="comics.php?comic=<?=$i + 1?>">
                            <?=basename($comics[$i])?>
                        </a>
                    </td>
                    <td>
                        <?=date('d.m.Y H:i:s', filemtime($comics[$i]))?>
                    </td>
                </tr>
<?php } ?>
            </table>
        </div>
    </body>
</html>

