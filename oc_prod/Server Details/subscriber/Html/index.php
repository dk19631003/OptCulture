<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"`>

<?php
 $url= $_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
$onhttps = $_SERVER['HTTPS'];

if ($url=="app.optculture.cloud/" || $url=="www.app.optculture.cloud/" ) {

if($onhttps=="on") {
   header("Location: https://app.optculture.cloud/subscriber" );
 } else {
   header("Location: http://app.optculture.cloud/subscriber" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="app.optculture.com/" || $url=="www.app.optculture.com/" ) {

if($onhttps=="on") {
   header("Location: https://app.optculture.com/subscriber" );
 } else {
   header("Location: https://app.optculture.com/subscriber" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="loyalty.optculture.info/" || $url=="www.loyalty.optculture.info/" ) {

if($onhttps=="on") {
   header("Location: https://loyalty.optculture.info/Loyalty" );
 } else {
    header("Location: http://loyalty.optculture.info/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="gift.optculture.info/" || $url=="www.gift.optculture.info/" ) {

if($onhttps=="on") {
   header("Location: https://gift.optculture.info/Loyalty" );
 } else {
    header("Location: http://gift.optculture.info/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="loyalty.retailarabiaco.com/" || $url=="www.loyalty.retailarabiaco.com/" ) {

if($onhttps=="on") {
   header("Location: https://loyalty.retailarabiaco.com/Loyalty" );
 } else {
    header("Location: http://loyalty.retailarabiaco.com/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="lc.optculture.info/" || $url=="www.lc.optculture.info/" ) {

if($onhttps=="on") {
   header("Location: https://lc.optculture.info/Loyalty" );
 } else {
    header("Location: http://lc.optculture.info/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="loyalty.optculture.cloud/" || $url=="loyalty.optculture.cloud/" ) {

if($onhttps=="on") {
   header("Location: https://loyalty.optculture.cloud/Loyalty" );
 } else {
    header("Location: http://loyalty.optculture.cloud/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}


if ($url=="gift.optculture.cloud/" || $url=="www.gift.optculture.cloud/" ) {

if($onhttps=="on") {
   header("Location: https://gift.optculture.cloud/Loyalty" );
 } else {
    header("Location: http://gift.optculture.cloud/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="trainingp.optculture.info/" || $url=="trainingp.optculture.info/" ) {

if($onhttps=="on") {
   header("Location: https://trainingp.optculture.info/Loyalty" );
 } else {
    header("Location: http://trainingp.optculture.info/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="rewards.feltchicago.com/" || $url=="rewards.feltchicago.com/" ) {

if($onhttps=="on") {
   header("Location: https://rewards.feltchicago.com/Loyalty" );
 } else {
    header("Location: http://rewards.feltchicago.com/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="cptest.optculture.cloud/" || $url=="www.cptest.optculture.cloud/" ) {

if($onhttps=="on") {
   header("Location: https://cptest.optculture.cloud/Loyalty" );
 } else {
   header("Location: http://cptest.optculture.cloud/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="loyalty.mailcontent.info/" || $url=="www.loyalty.mailcontent.info/" ) {

if($onhttps=="on") {
   header("Location: https://loyalty.mailcontent.info/Loyalty" );
 } else {
    header("Location: http://loyalty.mailcontent.info/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="perks.amanasociety.com/" || $url=="www.perks.amanasociety.com/" ) {

if($onhttps=="on") {
   header("Location: https://perks.amanasociety.com/Loyalty" );
 } else {
    header("Location: http://perks.amanasociety.com/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}

if ($url=="ocloyalty.optculture.cloud/" || $url=="www.ocloyalty.optculture.cloud/" ) {

if($onhttps=="on") {
   header("Location: https://ocloyalty.optculture.cloud/Loyalty" );
 } else {
   header("Location: https://ocloyalty.optculture.cloud/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}


if ($url=="fbb-oman.rewardz.app/" || $url=="www.fbb-oman.rewardz.app/" ) {

if($onhttps=="on") {
   header("Location: https://fbb-oman.rewardz.app/Loyalty" );
 } else {
   header("Location: http://fbb-oman.rewardz.app/Loyalty" );
 }


 #   header("Location: http://app.optculture.com/subscriber" );
    exit;
}


?>

<?php
echo $onhttps;
      include("optculture.html");
?>
