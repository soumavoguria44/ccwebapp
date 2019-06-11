#!/bin/bash
#******************************************************************************
#    AWS Networking Resources Creation Shell Script
#******************************************************************************
#
# SYNOPSIS
#    Automates the creation of a custom VPC, 3 subnets, gateway, Route Table.
#
# DESCRIPTION
#    This shell script leverages the AWS Command Line Interface (AWS CLI) to
#    automatically create a custom VPC and other realted resources.
#==============================================================================

# NOTES
#   VERSION:   0.1.0
#   LASTEDIT:  06/10/2019
#   AUTHOR:    Soumavo Guria
#   EMAIL:     guria.s@husky.neu.edu
#   REVISIONS:
#       0.1.0  06/10/2019 - first release
#
#==============================================================================
#   User provided Input
#==============================================================================
#
read -p "Enter your Region :" AWS_REGION
read -p "Enter your VPC Name :" VPC_NAME
read -p "Enter your VPC_CIDR :" VPC_CIDR

read -p "Enter your Subnet1 Public CIDR :" SUBNET1_PUBLIC_CIDR
read -p "Enter your Subnet1 Public VR :" SUBNET1_PUBLIC_VR
read -p "Enter your Subnet1 Public Name :" SUBNET1_PUBLIC_NAME

read -p "Enter your Subnet2 Public CIDR :" SUBNET2_PUBLIC_CIDR
read -p "Enter your Subnet2 Public VR :" SUBNET2_PUBLIC_VR
read -p "Enter your Subnet2 Public Name :" SUBNET2_PUBLIC_NAME

read -p "Enter your Subnet3 Public CIDR :" SUBNET3_PUBLIC_CIDR
read -p "Enter your Subnet3 Public VR :" SUBNET3_PUBLIC_VR
read -p "Enter your Subnet3 Public Name :" SUBNET3_PUBLIC_NAME
#

#Create VPC

echo "Creating VPC in preferred region"
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block $VPC_CIDR \
  --query 'Vpc.{VpcId:VpcId}' \
  --output text \
  --region $AWS_REGION)
echo "  VPC ID '$VPC_ID' CREATED in '$AWS_REGION' region."

# Add Name tag to VPC

aws ec2 create-tags \
  --resources $VPC_ID \
  --tags "Key=Name,Value=$VPC_NAME" \
  --region $AWS_REGION
echo "  VPC ID '$VPC_ID' NAMED as '$VPC_NAME'."

# Create Public Subnet1
echo "Creating Public Subnet..."
SUBNET1_PUBLIC_ID=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $SUBNET1_PUBLIC_CIDR \
  --availability-zone $SUBNET1_PUBLIC_VR \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $AWS_REGION)
echo "  Subnet ID '$SUBNET1_PUBLIC_ID' CREATED in '$SUBNET1_PUBLIC_VR'" \
"Availability Zone."

# Add Name tag to Public Subnet1
aws ec2 create-tags \
  --resources $SUBNET1_PUBLIC_ID \
  --tags "Key=Name,Value=$SUBNET1_PUBLIC_NAME" \
  --region $AWS_REGION
echo "  Subnet ID '$SUBNET1_PUBLIC_ID' NAMED as" \
"'$SUBNET1_PUBLIC_NAME'."


# Create Public Subnet2
SUBNET2_PUBLIC_ID=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $SUBNET2_PUBLIC_CIDR \
  --availability-zone $SUBNET2_PUBLIC_VR \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $AWS_REGION)
echo "  Subnet ID '$SUBNET2_PUBLIC_ID' CREATED in '$SUBNET2_PUBLIC_VR'" \
"Availability Zone."

# Add Name tag to Public Subnet2
aws ec2 create-tags \
  --resources $SUBNET2_PUBLIC_ID \
  --tags "Key=Name,Value=$SUBNET2_PUBLIC_NAME" \
  --region $AWS_REGION
echo "  Subnet ID '$SUBNET2_PUBLIC_ID' NAMED as" \
"'$SUBNET2_PUBLIC_NAME'."

# Create Public Subnet3
SUBNET3_PUBLIC_ID=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $SUBNET3_PUBLIC_CIDR \
  --availability-zone $SUBNET3_PUBLIC_VR \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $AWS_REGION)
echo "  Subnet ID '$SUBNET3_PUBLIC_ID' CREATED in '$SUBNET3_PUBLIC_VR'" \
"Availability Zone."

# Add Name tag to Public Subnet3
aws ec2 create-tags \
  --resources $SUBNET3_PUBLIC_ID \
  --tags "Key=Name,Value=$SUBNET3_PUBLIC_NAME" \
  --region $AWS_REGION
echo "  Subnet ID '$SUBNET3_PUBLIC_ID' NAMED as" \
"'$SUBNET3_PUBLIC_NAME'."

# Create Internet gateway
echo "Creating Internet Gateway..."
IGW_ID=$(aws ec2 create-internet-gateway \
  --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
  --output text \
  --region $AWS_REGION)
echo "  Internet Gateway ID '$IGW_ID' CREATED."

# Attach Internet gateway to your VPC
aws ec2 attach-internet-gateway \
  --vpc-id $VPC_ID \
  --internet-gateway-id $IGW_ID \
  --region $AWS_REGION
echo "  Internet Gateway ID '$IGW_ID' ATTACHED to VPC ID '$VPC_ID'."


# Create Route Table
echo "Creating Route Table..."
ROUTE_TABLE_ID=$(aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --query 'RouteTable.{RouteTableId:RouteTableId}' \
  --output text \
  --region $AWS_REGION)
echo "  Route Table ID '$ROUTE_TABLE_ID' CREATED."

# Create route to Internet Gateway
RESULT=$(aws ec2 create-route \
  --route-table-id $ROUTE_TABLE_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID \
  --region $AWS_REGION)
echo "  Route to '0.0.0.0/0' via Internet Gateway ID '$IGW_ID' ADDED to" \
"Route Table ID '$ROUTE_TABLE_ID'."

# Associate Public Subnet1 with Route Table
RESULT=$(aws ec2 associate-route-table  \
  --subnet-id $SUBNET1_PUBLIC_ID \
  --route-table-id $ROUTE_TABLE_ID \
  --region $AWS_REGION)
echo "  Public Subnet ID '$SUBNET1_PUBLIC_ID' ASSOCIATED with Route Table ID" \
"'$ROUTE_TABLE_ID'."

# Associate Public Subnet2 with Route Table
RESULT=$(aws ec2 associate-route-table  \
  --subnet-id $SUBNET2_PUBLIC_ID \
  --route-table-id $ROUTE_TABLE_ID \
  --region $AWS_REGION)
echo "  Public Subnet ID '$SUBNET2_PUBLIC_ID' ASSOCIATED with Route Table ID" \
"'$ROUTE_TABLE_ID'."

# Associate Public Subnet3 with Route Table
RESULT=$(aws ec2 associate-route-table  \
  --subnet-id $SUBNET3_PUBLIC_ID \
  --route-table-id $ROUTE_TABLE_ID \
  --region $AWS_REGION)
echo "  Public Subnet ID '$SUBNET3_PUBLIC_ID' ASSOCIATED with Route Table ID" \
"'$ROUTE_TABLE_ID'."



echo "COMPLETED"
